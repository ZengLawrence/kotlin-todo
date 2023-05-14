package controller

import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)
data class NewTodoDto(val description: String)

data class PatchTodoDto(val done: Boolean?)

data class IdDto(val id: Int)

val todos = mutableListOf<TodoDto>()

object Controller {

    @OpenApi(
        summary = "Get all todos",
        tags = ["Read-only"],
        responses = [OpenApiResponse("200", [OpenApiContent(Array<TodoDto>::class)])],
        path = "/todos",
        methods = [HttpMethod.GET]
    )
    fun getAll(ctx: Context) {
        ctx.json(todos)
    }

    @OpenApi(
        summary = "Get a todo",
        tags = ["Read-only"],
        responses = [OpenApiResponse("200", [OpenApiContent(TodoDto::class)]),
                    OpenApiResponse("404")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class)],
        methods = [HttpMethod.GET]
    )
    fun get(ctx: Context) {
        todos.find { it.id == ctx.pathParam("id").toInt() }
            ?.also { ctx.json(it) } ?: ctx.status(HttpStatus.NOT_FOUND)
    }

    @OpenApi(
        summary = "create a new todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(NewTodoDto::class)], required = true),
        responses = [OpenApiResponse("201", [OpenApiContent(IdDto::class)])],
        path = "/todos",
        methods = [HttpMethod.POST]
    )
    fun create(ctx: Context) {
        val request = ctx.bodyAsClass<NewTodoDto>()
        val nextId = todos.maxOfOrNull(TodoDto::id)?.plus(1) ?: 1
        val newTodoDto = TodoDto(nextId, request.description)
        todos += newTodoDto
        ctx.json(IdDto(nextId))
            .status(HttpStatus.CREATED)
    }

    @OpenApi(
        summary = "Toggle done flag on a todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(PatchTodoDto::class)], required = true),
        responses = [OpenApiResponse("204")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class)],
        methods = [HttpMethod.PATCH]
    )
    fun update(ctx: Context) {
        ctx.bodyAsClass<PatchTodoDto>().done?.let { done ->
            todos.find { it.id == ctx.pathParam("id").toInt() }
                ?.also {
                    todos.remove(it)
                    val updated = it.copy(done = done)
                    todos.add(updated)
                }
        }

        ctx.status(HttpStatus.NO_CONTENT)
    }

    @OpenApi(
        summary = "Delete a todo",
        tags = ["Mutation"],
        responses = [OpenApiResponse("204")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class)],
        methods = [HttpMethod.DELETE]
    )
    fun delete(ctx: Context) {
            todos.removeIf { it.id == ctx.pathParam("id").toInt() }
            ctx.status(HttpStatus.NO_CONTENT)
    }

}