package history.domain

import java.time.ZonedDateTime

sealed interface Event {
    val todoId: Int
    val timestamp: ZonedDateTime
}

data class AddEvent(
    override val todoId: Int,
    val description: String,
    override val timestamp: ZonedDateTime,
): Event

data class CheckDoneEvent(
    override val todoId: Int,
    override val timestamp: ZonedDateTime,
): Event

data class UncheckDoneEvent(
    override val todoId: Int,
    override val timestamp: ZonedDateTime,
): Event

data class DeleteEvent(
    override val todoId: Int,
    override val timestamp: ZonedDateTime,
): Event

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
