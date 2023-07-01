package spring

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import todo.PTodo
import todo.TodoPersistence

fun Task.toPTodo() = PTodo(id!!, description, done)

@Service
class TodoDBService(val repository: TodoRepository): TodoPersistence {
    override fun insert(description: String, done: Boolean): Int =
        repository.save(Task(null, description, done)).id!!

    override fun update(id: Int, done: Boolean) {
        repository.findById(id).ifPresent {
            val updated = it.copy(done = done)
            repository.save(updated)
        }
    }

    override fun delete(id: Int) {
        repository.deleteById(id)
    }

    override fun find(id: Int): PTodo? =
        repository.findByIdOrNull(id)?.toPTodo()

    override fun findAll(): List<PTodo> =
        repository.findAll().map(Task::toPTodo)
}