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
        jedis.hset("todo:$id", value)
        return id
    }

    override fun update(id: Int, done: Boolean) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun find(id: Int): PTodo? {
        val map = jedis.hgetAll("todo:$id")
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
}