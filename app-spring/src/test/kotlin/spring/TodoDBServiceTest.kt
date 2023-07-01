package spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import todo.AbstractTodoPersistenceTest
import todo.TodoPersistence


@SpringBootTest
@TestPropertySource(properties = [
    "spring.test.database.replace=none",
])
@Testcontainers
class TodoDBServiceTest: AbstractTodoPersistenceTest() {

    companion object {

        @JvmStatic
        @Container
        var postgres = PostgreSQLContainer("postgres:15.3")
            .withCopyToContainer(
                MountableFile.forClasspathResource("sql/init-db.sql"),
                "/docker-entrypoint-initdb.d/init-db.sql"
            )

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Autowired
    lateinit var todoRepository: TodoRepository

    override fun persistence(): TodoPersistence = TodoDBService(todoRepository)

}