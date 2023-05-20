package todo

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