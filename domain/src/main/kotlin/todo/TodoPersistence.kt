package todo

import arrow.core.Either
import arrow.core.Either.*

data class PTodo(
    val id: Int,
    val description: String,
    val done: Boolean
)
interface TodoPersistence {

    fun insert(description: String, done: Boolean): Int

    fun update(id: Int, done: Boolean)

    fun delete(id: Int)

    fun find(id: Int): PTodo?
    fun findAll(): List<PTodo>

}

class FunctionalTodoPersistence(private val delegate: TodoPersistence) {

    private fun <T> wrapRuntimeError(func: () -> T): Either<RuntimeError, T> = try {
        Right(func())
    } catch (ex: RuntimeException) {
        Left(RuntimeError(ex.message ?: ""))
    }

    private fun <T> wrapNullableRuntimeError(func: () -> T?): Either<RuntimeError, T>? = try {
        func()?.let { Right(it) }
    } catch (ex: RuntimeException) {
        Left(RuntimeError(ex.message ?: ""))
    }

    fun insert(description: String, done: Boolean) = wrapRuntimeError {
        delegate.insert(description, done)
    }


    fun update(id: Int, done: Boolean) = wrapRuntimeError {
        delegate.update(id, done)
    }

    fun delete(id: Int) = wrapRuntimeError {
        delegate.delete(id)
    }

    fun find(id: Int) = wrapNullableRuntimeError {
        delegate.find(id)
    }

    fun findAll() = wrapRuntimeError {
        delegate.findAll()
    }

}