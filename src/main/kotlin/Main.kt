import controller.Controller
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.HttpStatus
import io.javalin.openapi.plugin.OpenApiConfiguration
import io.javalin.openapi.plugin.OpenApiPlugin
import io.javalin.openapi.plugin.redoc.ReDocConfiguration
import io.javalin.openapi.plugin.redoc.ReDocPlugin
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration
import io.javalin.openapi.plugin.swagger.SwaggerPlugin

fun main() {
    Javalin.create { config ->
        config.plugins.register(OpenApiPlugin(OpenApiConfiguration().apply {
            info.title = "Todo REST Service"
        }))
        config.plugins.register(SwaggerPlugin(SwaggerConfiguration()))
        config.plugins.register(ReDocPlugin(ReDocConfiguration()))
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