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

fun AddEvent.toTodo() = Todo(
    this.todoId,
    this.description,
    done = false,
    lastUpdatedDateTime = this.timestamp,
)

private fun find(eventSource: EventSource, id: Int): Todo? =
    eventSource.findEvents(id)
        .fold(null as Todo?) { todo, event ->
            when(event) {
                is AddEvent -> event.toTodo()
                is CheckDoneEvent -> todo?.copy(done = true, lastUpdatedDateTime = event.timestamp)
                is UncheckDoneEvent -> todo?.copy(done = false, lastUpdatedDateTime = event.timestamp)
                is DeleteEvent -> null
            }
        }
