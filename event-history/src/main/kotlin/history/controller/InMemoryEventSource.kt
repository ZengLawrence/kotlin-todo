package history.controller

import history.domain.Event
import history.domain.EventSource
import java.time.ZonedDateTime

object InMemoryEventSource: EventSource {
    override fun findTodoIds(): List<Int> =
        listOf(1, 2, 3)

    override fun findEvents(todoId: Int): List<Event> = listOf(
        Event("ADD", ZonedDateTime.now()),
        Event("CHECKED_DONE", ZonedDateTime.now().plusMinutes(30)),
    )
}