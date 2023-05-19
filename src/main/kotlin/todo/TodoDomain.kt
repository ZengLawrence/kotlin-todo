package todo

data class Todo(
        val id: Int,
        val description: String,
        val done: Boolean = false)

val todos = mutableListOf<Todo>();

object TodoDomain {

    private fun nextId() = todos.maxOfOrNull(Todo::id)?.plus(1) ?: 1

    fun add(description: String): Int {
        val id = nextId()
        todos += Todo(id, description)
        return id
    }

    fun find(id: Int): Todo? = todos.find { it.id == id }

    fun findAll(): List<Todo> = todos.toList()

     fun toggleDone(id: Int, done: Boolean) {
        find(id)?.also {
                todos.remove(it)
                val updated = it.copy(done = done)
                todos.add(updated)
            }
    }

    fun delete(id: Int): Boolean = todos.removeIf { it.id == id }

}