package todo

data class Todo(
        val id: Int,
        val description: String,
        val done: Boolean = false)

private fun PTodo.toDomain() =
    Todo(this.id, this.description, this.done)

class TodoDomain(private val persistence: TodoPersistence) {

    fun add(description: String): Int {
        return persistence.insert(description, done = false)
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