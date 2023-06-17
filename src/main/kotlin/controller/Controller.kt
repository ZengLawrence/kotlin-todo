package controller

import arrow.core.Either
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*
import todo.Todo
import todo.TodoDomain

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)
data class NewTodoDto(val description: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PatchTodoDto(val done: Boolean?)

data class IdDto(val id: Int)

data class Error(val errorDescription: String)

private fun Todo.toDto(): TodoDto {
    return TodoDto(this.id, this.description, this.done)
}

class Controller(private val todoDomain: TodoDomain) {

    @OpenApi(
        summary = "Get all todos",
        tags = ["Read-only"],
        responses = [
            OpenApiResponse("200", [OpenApiContent(Array<TodoDto>::class)]),
            OpenApiResponse("500")
                    ],
        path = "/todos",
        methods = [HttpMethod.GET]
    )
    fun getAll(ctx: Context) {
        ctx.json(todoDomain.findAll().map(Todo::toDto))
    }

    @OpenApi(
        summary = "Get a todo",
        tags = ["Read-only"],
        responses = [
            OpenApiResponse("200", [OpenApiContent(TodoDto::class)]),
            OpenApiResponse("404"),
            OpenApiResponse("500")
                    ],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class, required = true)],
        methods = [HttpMethod.GET]
    )
    fun get(ctx: Context) {
        todoDomain.find(ctx.pathParam("id").toInt())
            ?.also { ctx.json(it.toDto()) } ?: ctx.status(HttpStatus.NOT_FOUND)
    }

    @OpenApi(
        summary = "create a new todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(NewTodoDto::class)], required = true),
        responses = [
            OpenApiResponse("201", [OpenApiContent(IdDto::class)]),
            OpenApiResponse("400"),
            OpenApiResponse("500")
                    ],
        path = "/todos",
        methods = [HttpMethod.POST]
    )
    fun create(ctx: Context) {
        val request = ctx.bodyAsClass<NewTodoDto>()
        val id = todoDomain.add(request.description)
        when(id) {
            is Either.Right -> ctx.json(IdDto(id.value))
                .status(HttpStatus.CREATED)
            else -> ctx.status(HttpStatus.BAD_REQUEST)
        }
    }

    @OpenApi(
        summary = "Toggle done flag on a todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(PatchTodoDto::class)], required = true),
        responses = [OpenApiResponse("204"), OpenApiResponse("500")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class, required = true)],
        methods = [HttpMethod.PATCH]
    )
    fun update(ctx: Context) {
        val id = ctx.pathParam("id").toInt()
        val patchTodoDto = ctx.bodyAsClass<PatchTodoDto>()
        if (patchTodoDto.done != null) {
            todoDomain.toggleDone(id, patchTodoDto.done)
            ctx.status(HttpStatus.NO_CONTENT)
        } else {
            ctx.json(Error("'done' attribute is not provided"))
                .status(HttpStatus.BAD_REQUEST)
        }
    }

    @OpenApi(
        summary = "Delete a todo",
        tags = ["Mutation"],
        responses = [OpenApiResponse("204"), OpenApiResponse("500")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class, required = true)],
        methods = [HttpMethod.DELETE]
    )
    fun delete(ctx: Context) {
            todoDomain.delete(ctx.pathParam("id").toInt())
            ctx.status(HttpStatus.NO_CONTENT)
    }

}