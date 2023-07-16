package changenotification

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.*

class MockQueueConsumer {

    private lateinit var consumer: KafkaConsumer<String, String>

    fun init() {
        val props = Properties()
        props.setProperty("bootstrap.servers", "PLAINTEXT_HOST://localhost:29092")
        props.setProperty("group.id", "int-test")
        props.setProperty("group.instance.id", "int-test-instance-1")
        props.setProperty("enable.auto.commit", "true")
        props.setProperty("auto.commit.interval.ms", "100")
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.setProperty("auto.offset.reset", "earliest")
        consumer = KafkaConsumer<String, String>(props)
        consumer.subscribe(listOf("todo_change_notification"))
    }

    fun drain() {
        consumer.poll(Duration.ofMillis(1000))
    }

    fun poll() = consumer.poll(Duration.ofMillis(5000)).first().value()

    fun close() {
        consumer.close()
    }

}