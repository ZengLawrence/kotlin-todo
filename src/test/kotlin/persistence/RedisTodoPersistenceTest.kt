package persistence

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.Rule
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import redis.clients.jedis.JedisPooled
import kotlin.test.Test


@Testcontainers
class RedisTodoPersistenceTest {

    @Container
    val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:alpine"))
        .withExposedPorts(6379)

    private var persistence: RedisTodoPersistence? = null

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        val jedis = JedisPooled(redis.host, redis.getMappedPort(6379))
        persistence = RedisTodoPersistence(jedis)
    }


    @Test
    fun `insert multiple times should get different id each time`() {
        val id1 = persistence!!.insert("Buy milk", done = false)
        val id2 = persistence!!.insert("Get mail", done = true)
        assertThat(id1).isNotEqualTo(id2)
    }
}