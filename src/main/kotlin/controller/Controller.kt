package controller

import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)
data class NewTodoDto(val description: String)

val todos = mutableListOf<TodoDto>(
    TodoDto(1, "Buy milk"),
    TodoDto(2, "Buy bread"),
    TodoDto(3, "Take out trash"),
)
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
        responses = [OpenApiResponse("201")],
        path = "/todos",
        methods = [HttpMethod.POST]
    )
    fun create(ctx: Context) {
        val request = ctx.bodyAsClass<NewTodoDto>()
        val nextId = todos.maxOfOrNull(TodoDto::id)?.plus(1) ?: 1
        val newTodoDto = TodoDto(nextId, request.description)
        todos += newTodoDto
        ctx.status(HttpStatus.CREATED)
    }

    @OpenApi(
        summary = "Update a todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(TodoDto::class)], required = true),
        responses = [OpenApiResponse("204")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class)],
        methods = [HttpMethod.PUT]
    )
    fun update(ctx: Context) {
        val todoDto = ctx.bodyAsClass<TodoDto>()
        todos.find { it.id == ctx.pathParam("id").toInt() }
            ?.also {
                todos.remove(it)
                todos.add(todoDto)
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