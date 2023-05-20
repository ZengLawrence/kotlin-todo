import controller.Controller
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.HttpStatus
import io.javalin.openapi.plugin.OpenApiPlugin
import io.javalin.openapi.plugin.OpenApiPluginConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerPlugin
import persistence.InMemoryTodoPersistence
import persistence.RedisTodoPersistence
import todo.TodoDomain

class App(todoDomain: TodoDomain) {

    private val controller = Controller(todoDomain)

    private val instance: Javalin = Javalin.create { config ->
        config.plugins.register(OpenApiPlugin(OpenApiPluginConfiguration()
            .withDefinitionConfiguration { _, definition ->
                definition.withOpenApiInfo { openApiInfo ->
                    openApiInfo.title = "Todo REST Service"
                    openApiInfo.version = "1.0.0"
                }
            }
        ))
        config.plugins.register(SwaggerPlugin(SwaggerConfiguration()))
    }.apply {
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(HttpStatus.NOT_FOUND) { ctx -> ctx.json("not found") }
    }.routes {
        ApiBuilder.path("todos") {
            ApiBuilder.get(controller::getAll)
            ApiBuilder.post(controller::create)
            ApiBuilder.path("{id}") {
                ApiBuilder.get(controller::get)
                ApiBuilder.patch(controller::update)
                ApiBuilder.delete(controller::delete)
            }
        }
    }!!

    fun start(port: Int) {
        instance.start(port)
    }

    fun stop() {
        instance.stop()
    }

    companion object {

        fun app(init: AppBuilder.() -> Unit): AppBuilder {
            return AppBuilder().apply(init)
        }

    }

    class AppBuilder {

        private var db: DB? = null

        fun build(): App {
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
}

data class DBConfig(val host: String, val port: Int)
data class AppConfig(val dbConfig: DBConfig? = null)
