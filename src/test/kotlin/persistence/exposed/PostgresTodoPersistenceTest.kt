package persistence.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import todo.AbstractTodoPersistenceTest

class PostgresTodoPersistenceTest: AbstractTodoPersistenceTest() {

    @Container
    val postgres = PostgreSQLContainer("postgres:15.3")
        .withDatabaseName("todo")
        .withUsername("postgres")
        .withPassword("postgres")

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        postgres.start()

        Database.connect(
            postgres.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgres.username,
            password = postgres.password
        )
        transaction {
            SchemaUtils.create(TTasks)
        }

        this.persistence = ExposedTodoPersistence()
    }
}