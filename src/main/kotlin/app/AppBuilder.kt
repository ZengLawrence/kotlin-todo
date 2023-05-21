package app

import persistence.InMemoryTodoPersistence
import persistence.RedisTodoPersistence
import todo.TodoDomain

interface Builder<T> {
    fun build(): T
}

class AppBuilder: Builder<App> {

    private var db: DB? = null

    override fun build(): App {
        return db?.let { db -> App(TodoDomain(RedisTodoPersistence.create(db.host, db.port))) }
            ?: App(TodoDomain(InMemoryTodoPersistence()))
    }

    fun db(init: DB.() -> Unit) {
        db = DB().apply(init)
    }
}

class DB {

    internal var host: String = "localhost"
    internal var port: Int = 0

    fun host(host: String) {
        this.host = host
    }

    fun port(port: Int) {
        this.port = port
    }
}
