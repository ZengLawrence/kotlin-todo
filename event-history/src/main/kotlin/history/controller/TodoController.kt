package history.controller

import history.domain.TodoDomain
import io.javalin.http.Context
import io.javalin.http.HttpStatus

interface TodoController {
    fun findTodo(ctx: Context)
}

object TodoControllerDsl {

    fun todoController(fn: () -> TodoDomain) = object: TodoController {
        override fun findTodo(ctx: Context) = findTodo(fn(), ctx)
    }
}

private fun findTodo(todoDomain: TodoDomain, ctx: Context) {
    val id = ctx.pathParam("id").toInt()
    todoDomain.find(id)?.also {
        ctx.json(it)
    } ?: ctx.status(HttpStatus.NOT_FOUND)
}
