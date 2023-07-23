package history

import com.fasterxml.jackson.databind.SerializationFeature
import history.controller.EventController
import history.controller.EventControllerDsl.eventController
import history.controller.TodoController
import history.controller.TodoControllerDsl.todoController
import history.domain.EventSource
import history.domain.HistoryDomainDsl.historyDomain
import history.domain.TodoDomainDsl.todoDomain
import io.javalin.Javalin
import io.javalin.json.JavalinJackson

class AppBuilder {

    lateinit var eventSource: EventSource

    fun start(port: Int): Javalin = create(
        eventController {
            historyDomain {
                eventSource
            }
        }, todoController {
            todoDomain {
                eventSource
            }
        }).start(port)


    companion object {
        fun app(init: AppBuilder.() -> Unit) =
            AppBuilder().apply(init)
    }
}

private fun create(eventController: EventController, todoController: TodoController) =
    Javalin.create { config ->
        with(config) {
            routing.contextPath = "/api"
            jsonMapper(JavalinJackson().updateMapper { mapper ->
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            })
        }
    }.get("/events", eventController::todoIds)
        .get("/events/todos/{id}", eventController::events)
        .get("/todos/{id}", todoController::findTodo)
