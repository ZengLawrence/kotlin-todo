package todo

import org.assertj.core.api.Assertions
import kotlin.test.Test

abstract class AbstractTodoPersistenceTest {

    protected lateinit var persistence: TodoPersistence

    @Test
    fun `insert multiple times should get different id each time`() {
        val id1 = persistence.insert("Buy milk", done = false)
        val id2 = persistence.insert("Get mail", done = true)
        Assertions.assertThat(id1).isNotEqualTo(id2)
    }

    @Test
    fun `find by id should return a todo object`() {
        val id = persistence.insert("Buy milk", done = false)
        val actual = persistence.find(id)
        Assertions.assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))
    }

    @Test
    fun `find by id does not return an object`() {
        Assertions.assertThat(persistence.find(-1)).isNull()
    }

    @Test
    fun `delete a todo should not return one`() {
        val id = persistence.insert("Buy milk", done = false)
        val actual = persistence.find(id)
        Assertions.assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))

        persistence.delete(id)
        Assertions.assertThat(persistence.find(id)).isNull()
    }

    @Test
    fun `update todo to done should return with done to true`() {
        val id = persistence.insert("Buy milk", done = false)
        val actual = persistence.find(id)
        Assertions.assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))

        persistence.update(id, done = true)
        Assertions.assertThat(persistence.find(id)).isEqualTo(PTodo(id, "Buy milk", done = true))
    }

    @Test
    fun `find all should return all todos`() {
        val id1 = persistence.insert("Buy milk", done = false)
        val id2 = persistence.insert("Get newspaper", done = true)
        val id3 = persistence.insert("Eat lunch", done = false)

        Assertions.assertThat(persistence.findAll()).containsOnlyElementsOf(
            listOf(
                PTodo(id1, "Buy milk", done = false),
                PTodo(id2, "Get newspaper", done = true),
                PTodo(id3, "Eat lunch", done = false)
            )
        )
    }
}