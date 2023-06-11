package app

import persistence.RedisTodoPersistence
import todo.TodoDomain
import todo.TodoPersistence

interface Builder<T> {
    fun build(): T
}

/**
 * DSL for building App. All components should implement Builder<T> interface.
 */
class AppBuilder: Builder<App> {

    // default db implementation to be in memory.
    private var dbBuilder: Builder<TodoPersistence> = RedisBuilder()

    override fun build(): App = App(TodoDomain(dbBuilder.build()))

    fun redis(init: RedisBuilder.() -> Unit) {
        dbBuilder = RedisBuilder().apply(init)
    }
}

class RedisBuilder(
    var host: String = "localhost",
    var port: Int = 6379
): Builder<TodoPersistence> {
    override fun build(): TodoPersistence = RedisTodoPersistence.create(host, port)
}
