package persistence

import redis.clients.jedis.JedisPooled
import redis.clients.jedis.UnifiedJedis
import todo.PTodo
import todo.TodoPersistence

/**
 * Redis based persistence implementation. This implementation is used for local develop only and
 * not intend to be efficient.
 */
class RedisTodoPersistence(private val jedis: UnifiedJedis): TodoPersistence {
    override fun insert(description: String, done: Boolean): Int {
        val id = jedis.incr("todo-id").toInt()
        val value = mapOf<String, String>(
            "id" to "$id",
            "description" to description,
            "done" to "$done"
        )
        jedis.hset(key(id), value)
        jedis.rpush("todo-ids", "$id")
        return id
    }

    override fun update(id: Int, done: Boolean) {
        jedis.hset(key(id), "done", "$done")
    }

    override fun delete(id: Int) {
        jedis.lrem("todo-ids", 1, "$id")
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
        return jedis.lrange("todo-ids", 0, -1).map {
            find(it.toInt())!!
        }
    }

    private fun key(id: Int) = "todo:$id"

    companion object {
        fun create(host: String, port: Int) = RedisTodoPersistence(JedisPooled(host, port))
    }
}