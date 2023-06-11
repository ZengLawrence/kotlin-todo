package persistence.exposed

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import persistence.exposed.ExposedTodoPersistence.Companion.setUpDbConnection
import todo.AbstractTodoPersistenceTest
import kotlin.random.Random

class PostgresTodoPersistenceTest: AbstractTodoPersistenceTest() {

    @Container
    val postgres = PostgreSQLContainer("postgres:15.3")
        .withDatabaseName("todo")
        .withUsername("postgres")
        .withPassword(Random.nextInt(100, 99999).toString())

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        postgres.start()

        setUpDbConnection(
            postgres.jdbcUrl,
            driver = "org.postgresql.Driver",
            username = postgres.username,
            password = postgres.password
        )

        this.persistence = ExposedTodoPersistence()
    }
}