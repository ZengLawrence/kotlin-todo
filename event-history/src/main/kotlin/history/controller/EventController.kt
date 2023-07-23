package history.controller

import history.domain.Event
import history.domain.HistoryDomain
import io.javalin.http.Context
import java.time.ZonedDateTime

data class TodoDto(
    val todoId: Int,
)

data class EventDto(
    val type: String,
    val timestamp: ZonedDateTime,
)

private fun Event.toEventDto() = EventDto(
    this.type,
    this.timestamp,
)

interface EventController {
    fun todoIds(ctx: Context)
    fun events(ctx: Context)
}

object EventControllerDsl {

    fun eventController(fn: () -> HistoryDomain) = object: EventController {
        override fun todoIds(ctx: Context) =
            todoIds(fn(), ctx)

        override fun events(ctx: Context) =
            events(fn(), ctx)
        }

}

private fun todoIds(historyDomain: HistoryDomain,  ctx: Context) {
    historyDomain.findTodoIds().map { todoId ->
        TodoDto(todoId)
    }.also {
        ctx.json(it)
    }
}

private fun events(historyDomain: HistoryDomain, ctx: Context) {
    val id = ctx.pathParam("id").toInt()
    historyDomain.findEvents(id)
        .map(Event::toEventDto)
        .also {
            ctx.json(it)
        }
}
