package controller

import io.javalin.http.Context
import io.javalin.http.bodyAsClass

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)
data class NewTodoDto(val description: String)

val todos = mutableListOf<TodoDto>(
    TodoDto(1, "Buy milk"),
    TodoDto(2, "Buy bread"),
    TodoDto(3, "Take out trash"),
)
object Controller {

    fun getAll(ctx: Context) {
        ctx.json(todos)
    }

    fun get(ctx: Context) {
        ctx.json(todos.filter { it.id == ctx.pathParam("id").toInt() })
    }

    fun create(ctx: Context) {
        val request = ctx.bodyAsClass<NewTodoDto>()
        val nextId = todos.maxOfOrNull(TodoDto::id)?.plus(1) ?: 1
        val newTodoDto = TodoDto(nextId, request.description)
        todos += newTodoDto
        ctx.status(201)
    }

    fun update(ctx: Context) {
        val todoDto = ctx.bodyAsClass<TodoDto>()
        todos.find { it.id == ctx.pathParam("id").toInt() }
            ?.also {
                todos.remove(it)
                todos.add(todoDto)
            }
        ctx.status(204)
    }

    fun delete(ctx: Context) {
            todos.removeIf { it.id == ctx.pathParam("id").toInt() }
            ctx.status(204)
    }

}