package history.domain

import java.time.ZonedDateTime

data class Event(
    val type: String,
    val timestamp: ZonedDateTime,
)

interface HistoryDomain {
    fun findTodoIds(): List<Int>
    fun findEvents(todoId: Int): List<Event>
}

object HistoryDomainDsl {

    fun historyDomain(eventSource: () -> EventSource) = object: HistoryDomain {
        override fun findTodoIds(): List<Int> =
            findTodoIds(eventSource())

        override fun findEvents(todoId: Int): List<Event> =
            findEvents(eventSource(), todoId)

    }

}

private fun findTodoIds(eventSource: EventSource) = eventSource.findTodoIds()

private fun findEvents(eventSource: EventSource, todoId: Int) =
    eventSource.findEvents(todoId)
