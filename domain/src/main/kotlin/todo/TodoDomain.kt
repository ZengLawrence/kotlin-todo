package todo

import arrow.core.Either
import arrow.core.Either.*
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import todo.Todo.Companion.validateDescription

sealed interface TodoError
object EmptyTodoDescription: TodoError
object TooLongDescription: TodoError
data class RuntimeError(val msg: String): TodoError

data class Todo private constructor(
        val id: Int,
        val description: String,
        val done: Boolean) {

    companion object {

        operator fun invoke(
            id: Int,
            description: String,
            done: Boolean = false
        ): Either<TodoError, Todo> = validateDescription(description).map { validatedDesc ->
            Todo(id, validatedDesc, done)
        }

        fun validateDescription(description: String) = either {
            ensure(description.isNotEmpty()) { EmptyTodoDescription }
            ensure(description.length <= 100) { TooLongDescription }
            description
        }

    }
}

private fun PTodo.toDomain() =
    Todo(this.id, this.description, this.done)

class TodoDomain(
    persistence: TodoPersistence,
    changeNotification: ChangeNotification = LoggingChangeNotification()
) {

    private val persist = FunctionalTodoPersistence(persistence)

    private val notification: ChangeNotification =
        IgnoreRuntimeExceptionChangeNotification(changeNotification)

    fun add(description: String): Either<TodoError, Int> = when (val err = validateDescription(description)) {
        is Right -> persist.insert(description, done = false)
            .onRight { id ->
                notification.added(id, description)
            }

        is Left -> err
    }

    fun find(id: Int): Either<TodoError, Todo>? = persist.find(id)
        ?.flatMap(PTodo::toDomain)

    fun findAll(): List<Either<TodoError, Todo>> =
        when (val res = persist.findAll()) {
            is Right -> res.value.map(PTodo::toDomain)
            is Left -> listOf(res)
        }

    fun toggleDone(id: Int, done: Boolean): Either<TodoError, Unit> =
        persist.find(id)?.flatMap {
            persist.update(id, done)
                .onRight {
                    if (done) {
                        notification.checkedDone(id)
                    } else {
                        notification.uncheckedDone(id)
                    }
                }
        } ?: Right(Unit)


    fun delete(id: Int): Either<TodoError, Unit> = persist.delete(id)
        .onRight {
            notification.deleted(id)
        }

}