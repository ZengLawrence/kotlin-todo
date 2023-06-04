package persistence

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
class RedisTodoPersistenceTest : AbstractTodoPersistenceTest() {

    @Container
    val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:alpine"))
        .withExposedPorts(6379)

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        persistence = RedisTodoPersistence.create(redis.host, redis.getMappedPort(6379))
    }

}