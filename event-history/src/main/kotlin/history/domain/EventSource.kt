package history.domain

interface EventSource {

    fun findTodoIds(): List<Int>

    fun findEvents(todoId: Int): List<Event>
}