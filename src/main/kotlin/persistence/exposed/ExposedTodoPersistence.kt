package persistence.exposed

import org.jetbrains.exposed.sql.transactions.transaction
import todo.PTodo
import todo.TodoPersistence

class ExposedTodoPersistence: TodoPersistence {
    override fun insert(description: String, done: Boolean): Int = transaction {
        TTask.new {
            this.description = description
            this.done = done
        }.id.value
    }

    override fun update(id: Int, done: Boolean) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun find(id: Int): PTodo? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<PTodo> {
        TODO("Not yet implemented")
    }
}