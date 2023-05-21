package app

import persistence.InMemoryTodoPersistence
import persistence.RedisTodoPersistence
import todo.TodoDomain
import todo.TodoPersistence

interface Builder<T> {
    fun build(): T
}

class AppBuilder: Builder<App> {

    private val dbBuilder: DBBuilder = DBBuilder()

    override fun build(): App = App(TodoDomain(dbBuilder.build()))

    fun db(init: DBBuilder.() -> Unit) {
        dbBuilder.apply(init)
    }
}

class DBBuilder: Builder<TodoPersistence> {

    private var host: String = "localhost"
    private var port: Int = 0

    fun host(host: String) {
        this.host = host
    }

    fun port(port: Int) {
        this.port = port
    }

    override fun build(): TodoPersistence =
        if (port > 0) {
            RedisTodoPersistence.create(host, port)
        } else {
            InMemoryTodoPersistence()
        }

}
