package history

import com.fasterxml.jackson.databind.SerializationFeature
import history.controller.EventController
import history.controller.EventControllerDsl
import history.controller.TodoController
import history.controller.TodoControllerDsl
import history.domain.EventSource
import history.domain.HistoryDomainDsl.historyDomain
import history.domain.TodoDomainDsl.todoDomain
import io.javalin.Javalin
import io.javalin.json.JavalinJackson

class AppBuilder {

    lateinit var eventSource: EventSource

    fun start(port: Int): Javalin = createAppWith(
        eventController(eventSource)
        , todoController(eventSource)
    ).start(port)


    companion object {
        fun app(init: AppBuilder.() -> Unit) =
            AppBuilder().apply(init)
    }
}

private fun eventController(eventSource: EventSource) = EventControllerDsl.eventController {
    historyDomain {
        eventSource
    }
}

private fun todoController(eventSource: EventSource) = TodoControllerDsl.todoController {
    todoDomain {
        eventSource
    }
}

private fun createAppWith(eventController: EventController, todoController: TodoController) =
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
