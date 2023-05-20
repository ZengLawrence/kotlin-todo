package persistence

import todo.PTodo
import todo.TodoPersistence

class InMemoryTodoPersistence: TodoPersistence {

    private val todos = mutableListOf<PTodo>()

    private fun nextId() = todos.maxOfOrNull(PTodo::id)?.plus(1) ?: 1

    override fun insert(description: String, done: Boolean): Int {
        val id = nextId()
        todos += PTodo(id, description, done)
        return id
    }

    override fun update(id: Int, done: Boolean) {
        find(id)?.also {
            todos.remove(it)
            val updated = it.copy(done = done)
            todos.add(updated)
        }
    }

    override fun delete(id: Int): Unit {
        todos.removeIf { it.id == id }
    }

    override fun find(id: Int) = todos.find { it.id == id }

    override fun findAll(): List<PTodo> = todos.toList()
}