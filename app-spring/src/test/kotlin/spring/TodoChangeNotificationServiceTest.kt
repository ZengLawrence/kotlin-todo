package spring

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import todo.ChangeNotification
import java.time.Duration
import java.util.*
import kotlin.test.Test


@SpringBootTest
@ContextConfiguration(classes = [TodoChangeNotificationService::class, TestKafkaConfig::class])
@Testcontainers
class TodoChangeNotificationServiceTest {

    companion object {

        @JvmStatic
        @Container
        val kafka = KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0")
        )

        private lateinit var consumer: KafkaConsumer<String, String>

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
        }

        @JvmStatic
        @BeforeAll
        fun setUpConsumer() {
            val props = Properties()
            props.setProperty("bootstrap.servers", kafka.bootstrapServers)
            props.setProperty("group.id", "test")
            props.setProperty("group.instance.id", "test-instance-1")
            props.setProperty("enable.auto.commit", "true")
            props.setProperty("auto.commit.interval.ms", "100")
            props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
            props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
            props.setProperty("max.poll.records", "1")
            props.setProperty("auto.offset.reset", "earliest")
            consumer = KafkaConsumer<String, String>(props)
            consumer.subscribe(listOf("todo_change_notification"))
        }

        @JvmStatic
        @AfterAll
        fun shutDownConsumer() {
            consumer.close()
        }
    }

    @Autowired
    lateinit var changeNotification: ChangeNotification

    @Test
    fun added() {

        changeNotification.added(1, "Buy milk")

        val record = consumer.poll(Duration.ofMillis(1000)).first()
        assertThat(record.value()).isEqualTo("{id:1,description:\"Buy milk\",op:\"ADD\"}")
    }

    @Test
    fun checkedDone() {
        changeNotification.checkedDone(1)

        val record = consumer.poll(Duration.ofMillis(1000)).first()
        assertThat(record.value()).isEqualTo("{id:1,op:\"CHECKED_DONE\"}")
    }

    @Test
    fun uncheckedDone() {
        changeNotification.uncheckedDone(1)

        val record = consumer.poll(Duration.ofMillis(1000)).first()
        assertThat(record.value()).isEqualTo("{id:1,op:\"UNCHECKED_DONE\"}")
    }

    @Test
    fun deleted() {
        changeNotification.deleted(1)

        val record = consumer.poll(Duration.ofMillis(1000)).first()
        assertThat(record.value()).isEqualTo("{id:1,op:\"DELETED\"}")
    }
}

@TestConfiguration
class TestKafkaConfig {

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val props = mapOf<String, Any>(
            "bootstrap.servers" to TodoChangeNotificationServiceTest.kafka.bootstrapServers,
            "enable.auto.commit" to "true",
            "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>) =
        KafkaTemplate(producerFactory)

}
