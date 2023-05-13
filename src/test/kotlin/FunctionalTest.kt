import controller.TodoDto
import io.javalin.json.JavalinJackson
import io.javalin.testtools.JavalinTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionalTest {

    private val app = App().instance
    private val todosJson = JavalinJackson().toJsonString(
        listOf(
            TodoDto(1, "Buy milk"),
            TodoDto(2, "Buy bread"),
            TodoDto(3, "Take out trash"),
        ), List::class.java)

    @Test
    fun `fetch todos returns list of todos`() = JavalinTest.test(app) { _, client ->
        client.get("/todos").also {
            assertThat(it.code).isEqualTo(200)
            assertThat(it.body?.string()).isEqualTo(todosJson)
        }
    }

    @Test
    fun `get todo by id returns a todo`() = JavalinTest.test(app) { _, client ->
        val id = 1
        val todoJson = JavalinJackson().toJsonString(TodoDto(1, "Buy milk"), TodoDto::class.java)
        client.get("/todos/$id").also {
            assertThat(it.code).isEqualTo(200)
            assertThat(it.body?.string()).isEqualTo(todoJson)
        }
    }
}