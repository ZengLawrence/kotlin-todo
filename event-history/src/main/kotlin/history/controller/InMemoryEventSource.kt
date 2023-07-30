package history.controller

import history.domain.AddEvent
import history.domain.CheckDoneEvent
import history.domain.Event
import history.domain.EventSource
import java.time.ZonedDateTime

object InMemoryEventSource: EventSource {
    override fun findTodoIds(): List<Int> =
        listOf(1, 2, 3)

    override fun findEvents(todoId: Int): List<Event> = listOf(
        AddEvent(todoId, "Buy milk", ZonedDateTime.now()),
        CheckDoneEvent(todoId, ZonedDateTime.now().plusMinutes(30)),
    )
}