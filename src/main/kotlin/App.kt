import controller.Controller
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.HttpStatus
import io.javalin.openapi.plugin.OpenApiPlugin
import io.javalin.openapi.plugin.OpenApiPluginConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerPlugin
import persistence.InMemoryTodoPersistence
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
        fun create() = App(TodoDomain(InMemoryTodoPersistence()))
    }
}