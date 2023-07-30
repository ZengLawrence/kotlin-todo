package history.controller

import com.fasterxml.jackson.annotation.JsonInclude
import history.domain.*
import io.javalin.http.Context
import java.time.ZonedDateTime

data class TodoDto(
    val todoId: Int,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EventDto(
    val type: String,
    val timestamp: ZonedDateTime,
    val description: String?
)

private fun Event.toEventDto(): EventDto {
    val type = when(this) {
        is AddEvent -> "ADD"
        is CheckDoneEvent -> "CHECK_DONE"
        is UncheckDoneEvent -> "UNCHECK_DONE"
        is DeleteEvent -> "DELETE"
    }
    val desc = when(this) {
        is AddEvent -> this.description
        else -> null
    }
    return EventDto(
        type,
        this.timestamp,
        desc,
    )
}

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
