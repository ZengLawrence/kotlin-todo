package controller

import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)
data class NewTodoDto(val description: String)

val todos = mutableListOf<TodoDto>(
    TodoDto(1, "Buy milk"),
    TodoDto(2, "Buy bread"),
    TodoDto(3, "Take out trash"),
)
fun main() {

    val app = Javalin.create().apply {
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(HttpStatus.NOT_FOUND) { ctx -> ctx.json("not found") }
    }.start(7070)

    app.routes {

        get("/todos") { ctx ->
            ctx.json(todos)
        }

        get("/todos/{id}") { ctx ->
            ctx.json(todos.filter { it.id == ctx.pathParam("id").toInt() })
        }

        post("/todos") { ctx ->
            val request = ctx.bodyAsClass<NewTodoDto>()
            val nextId = todos.maxOfOrNull(TodoDto::id)?.plus(1) ?: 1
            val newTodoDto = TodoDto(nextId, request.description)
            todos += newTodoDto
            ctx.status(201)
        }

        patch("/todos/{id}") { ctx ->
            val todoDto = ctx.bodyAsClass<TodoDto>()
            todos.find { it.id == ctx.pathParam("id").toInt() }
                ?.also {
                    todos.remove(it)
                    todos.add(todoDto)
                }
            ctx.status(204)
        }

        delete("/todos/{id}") { ctx ->
            todos.removeIf { it.id == ctx.pathParam("id").toInt() }
            ctx.status(204)
        }

    }

}