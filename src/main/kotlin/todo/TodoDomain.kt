package todo

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import todo.Todo.Companion.validateDescription

sealed interface TodoError
object EmptyTodoDescription: TodoError
object TooLongDescription: TodoError

data class Todo(
        val id: Int,
        val description: String,
        val done: Boolean = false) {

    companion object {
        fun validateDescription(description: String) = either {
            ensure(description.isNotEmpty()) { EmptyTodoDescription }
            ensure(description.length <= 100) { TooLongDescription }
            description
        }

    }
}

private fun PTodo.toDomain() =
    Todo(this.id, this.description, this.done)

class TodoDomain(private val persistence: TodoPersistence) {

    fun add(description: String): Either<TodoError, Int> =
        validateDescription(description).map {
            persistence.insert(description, done = false)
        }


    fun find(id: Int): Todo? = persistence.find(id)?.toDomain()

    fun findAll(): List<Todo> = persistence.findAll().map(PTodo::toDomain)

    fun toggleDone(id: Int, done: Boolean) {
        find(id)?.also {
                persistence.update(id, done)
            }
    }

    fun delete(id: Int) = persistence.delete(id)

}