package app

import persistence.RedisTodoPersistence
import persistence.exposed.ExposedTodoPersistence
import persistence.exposed.ExposedTodoPersistence.Companion.setUpDbConnection
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

    fun postgres(init: PostgresBuilder.() -> Unit) {
        dbBuilder = PostgresBuilder().apply(init)
    }
}

class RedisBuilder(
    var host: String = "localhost",
    var port: Int = 6379
): Builder<TodoPersistence> {
    override fun build(): TodoPersistence = RedisTodoPersistence.create(host, port)
}

class PostgresBuilder(
    var host: String = "localhost",
    var port: Int = 5432,
    var db: String = "todo",
    var username: String = "postgres"
): Builder<TodoPersistence> {

    lateinit var password: String

    override fun build(): TodoPersistence {
        setUpDbConnection(
            jdbcUrl = "jdbc:postgresql://$host:$port/$db",
            driver = "org.postgresql.Driver",
            username = username,
            password = password
        )
        return ExposedTodoPersistence()
    }
}