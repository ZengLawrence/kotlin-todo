package todo

import org.assertj.core.api.Assertions.assertThat
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

abstract class AbstractTodoPersistenceTest {

    protected abstract fun persistence(): TodoPersistence

    @Test
    @Transactional
    fun `insert multiple times should get different id each time`() {
        val id1 = persistence().insert("Buy milk", done = false)
        val id2 = persistence().insert("Get mail", done = true)
        assertThat(id1).isNotEqualTo(id2)
    }

    @Test
    @Transactional
    fun `find by id should return a todo object`() {
        val id = persistence().insert("Buy milk", done = false)
        val actual = persistence().find(id)
        assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))
    }

    @Test
    @Transactional
    fun `find by id does not return an object`() {
        assertThat(persistence().find(-1)).isNull()
    }

    @Test
    @Transactional
    fun `delete a todo should not return one`() {
        val id = persistence().insert("Buy milk", done = false)
        val actual = persistence().find(id)
        assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))

        persistence().delete(id)
        assertThat(persistence().find(id)).isNull()
    }

    @Test
    @Transactional
    fun `update todo to done should return with done to true`() {
        val id = persistence().insert("Buy milk", done = false)
        val actual = persistence().find(id)
        assertThat(actual).isEqualTo(PTodo(id, "Buy milk", done = false))

        persistence().update(id, done = true)
        assertThat(persistence().find(id)).isEqualTo(PTodo(id, "Buy milk", done = true))
    }

    @Test
    @Transactional
    fun `find all should return all todos`() {
        val id1 = persistence().insert("Buy milk", done = false)
        val id2 = persistence().insert("Get newspaper", done = true)
        val id3 = persistence().insert("Eat lunch", done = false)

        assertThat(persistence().findAll()).containsOnlyElementsOf(
            listOf(
                PTodo(id1, "Buy milk", done = false),
                PTodo(id2, "Get newspaper", done = true),
                PTodo(id3, "Eat lunch", done = false)
            )
        )
    }
}