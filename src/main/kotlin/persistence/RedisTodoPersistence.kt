package persistence

import redis.clients.jedis.UnifiedJedis
import todo.PTodo
import todo.TodoPersistence

class RedisTodoPersistence(private val jedis: UnifiedJedis): TodoPersistence {
    override fun insert(description: String, done: Boolean): Int {
        val id = jedis.incr("todo-id").toInt()
        val value = mapOf<String, String>(
            "id" to "$id",
            "description" to description,
            "done" to "$done"
        )
        jedis.hset(key(id), value)
        return id
    }

    override fun update(id: Int, done: Boolean) {
        jedis.hset(key(id), "done", "$done")
    }

    override fun delete(id: Int) {
        jedis.del(key(id))
    }

    override fun find(id: Int): PTodo? {
        val map = jedis.hgetAll(key(id))
        return if (map.containsKey("id")) {
            assert(map["id"]?.toInt() == id)
            PTodo(id, map["description"] ?: "", map["done"].toBoolean())
        } else {
            null
        }
    }

    override fun findAll(): List<PTodo> {
        TODO("Not yet implemented")
    }

    private fun key(id: Int) = "todo:$id"
}