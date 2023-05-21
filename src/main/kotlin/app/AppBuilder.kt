package app

import persistence.InMemoryTodoPersistence
import persistence.RedisTodoPersistence
import todo.TodoDomain
import todo.TodoPersistence

interface Builder<T> {
    fun build(): T
}

class AppBuilder: Builder<App> {

    private var dbBuilder: Builder<TodoPersistence> = InMemoryDBBuilder()

    override fun build(): App = App(TodoDomain(dbBuilder.build()))

    fun redis(init: RedisBuilder.() -> Unit) {
        dbBuilder = RedisBuilder().apply(init)
    }
}

class RedisBuilder: Builder<TodoPersistence> {

    private var host: String = "localhost"
    private var port: Int = 6379

    fun host(host: String) {
        this.host = host
    }

    fun port(port: Int) {
        this.port = port
    }

    override fun build(): TodoPersistence = RedisTodoPersistence.create(host, port)

}

class InMemoryDBBuilder: Builder<TodoPersistence> {
    override fun build(): InMemoryTodoPersistence = InMemoryTodoPersistence()
}