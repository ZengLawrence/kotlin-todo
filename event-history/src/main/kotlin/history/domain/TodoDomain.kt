package history.domain

import java.time.ZonedDateTime

data class Todo (
    val id: Int,
    val description: String,
    val done: Boolean,
    val lastUpdatedDateTime: ZonedDateTime
)

interface TodoDomain {
    fun find(id: Int): Todo?
}

object TodoDomainDsl {

    fun todoDomain(fn: () -> EventSource) = object: TodoDomain {
            override fun find(id: Int): Todo? = find(fn(), id)
        }
}

private fun find(eventSource: EventSource, id: Int): Todo? =
    eventSource.findEvents(id)
        .fold(Todo(id, "", done = false, lastUpdatedDateTime = ZonedDateTime.now())) { todo, event ->
            when(event.type) {
                "CHECKED_DONE" -> todo.copy(done = true, lastUpdatedDateTime = event.timestamp)
                else -> todo.copy(lastUpdatedDateTime = event.timestamp)
            }
        }
