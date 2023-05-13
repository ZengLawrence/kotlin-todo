import controller.Controller
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.HttpStatus
import io.javalin.openapi.plugin.OpenApiPlugin
import io.javalin.openapi.plugin.OpenApiPluginConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerPlugin

fun main() {
    Javalin.create { config ->
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
        path("todos") {
            get(Controller::getAll)
            post(Controller::create)
            path("{id}") {
                get(Controller::get)
                put(Controller::update)
                delete(Controller::delete)
            }
        }
    }.start(7070)

}