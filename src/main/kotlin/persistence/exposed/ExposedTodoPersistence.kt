package persistence.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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

    override fun update(id: Int, done: Boolean): Unit = transaction {
        TTask.findById(id)?.also {
            it.done = done
        }
    }

    override fun delete(id: Int): Unit = transaction {
        TTask.findById(id)?.delete()
    }

    override fun find(id: Int): PTodo? = transaction {
        TTask.findById(id)?.toPTodo()
    }

    override fun findAll(): List<PTodo> = transaction {
        TTask.all().map(TTask::toPTodo)
    }

    companion object {
        fun setUpDbConnection(
            jdbcUrl: String,
            driver: String,
            username: String,
            password: String
        ) {

            Database.connect(
                jdbcUrl,
                driver = driver,
                user = username,
                password = password
            )
            transaction {
                SchemaUtils.create(TTasks)
            }

        }
    }
}