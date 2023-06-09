package controller

import arrow.core.Either
import arrow.core.separateEither
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.openapi.*
import todo.EmptyTodoDescription
import todo.Todo
import todo.TodoDomain
import todo.TooLongDescription

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
            OpenApiResponse("200", [OpenApiContent(Array<TodoDto>::class)])
                    ],
        path = "/todos",
        methods = [HttpMethod.GET]
    )
    fun getAll(ctx: Context) {
        todoDomain.findAll()
            .map { it.map(Todo::toDto) }
            .separateEither()
            .also { (_, todos) ->
                ctx.json(todos)
            }
    }

    @OpenApi(
        summary = "Get a todo",
        tags = ["Read-only"],
        responses = [
            OpenApiResponse("200", [OpenApiContent(TodoDto::class)]),
            OpenApiResponse("404"),
            OpenApiResponse("500", description = "Could caused by persistence contains invalid error, though this is rare.")
                    ],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class, required = true)],
        methods = [HttpMethod.GET]
    )
    fun get(ctx: Context) {
        val id = ctx.pathParam("id").toInt()
        todoDomain.find(id)
            ?.map(Todo::toDto)
            ?.also {
                when (it) {
                    is Either.Right -> ctx.json(it.value)
                    else -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
            ?: ctx.status(HttpStatus.NOT_FOUND)
    }

    @OpenApi(
        summary = "create a new todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(NewTodoDto::class)], required = true),
        responses = [
            OpenApiResponse("201", [OpenApiContent(IdDto::class)]),
            OpenApiResponse("400", [OpenApiContent(Error::class)])
                    ],
        path = "/todos",
        methods = [HttpMethod.POST]
    )
    fun create(ctx: Context) {
        val request = ctx.bodyAsClass<NewTodoDto>()
        val id = todoDomain.add(request.description)
        when (id) {
            is Either.Right -> ctx.json(IdDto(id.value))
                .status(HttpStatus.CREATED)

            is Either.Left -> when (id.value) {
                is EmptyTodoDescription -> ctx.json(Error("'description' attribute can not be empty"))
                    .status(HttpStatus.BAD_REQUEST)
                is TooLongDescription -> ctx.json(Error("'description' attribute can not be longer than 100 characters"))
                    .status(HttpStatus.BAD_REQUEST)
                else -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    @OpenApi(
        summary = "Toggle done flag on a todo",
        tags = ["Mutation"],
        requestBody = OpenApiRequestBody([OpenApiContent(PatchTodoDto::class)], required = true),
        responses = [
            OpenApiResponse("204"),
            OpenApiResponse("400", [OpenApiContent(Error::class)])
                    ],
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
        responses = [OpenApiResponse("204")],
        path = "/todos/{id}",
        pathParams = [OpenApiParam("id", Int::class, required = true)],
        methods = [HttpMethod.DELETE]
    )
    fun delete(ctx: Context) {
            todoDomain.delete(ctx.pathParam("id").toInt())
            ctx.status(HttpStatus.NO_CONTENT)
    }

}