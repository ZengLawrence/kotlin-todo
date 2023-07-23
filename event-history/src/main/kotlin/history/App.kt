package history

import com.fasterxml.jackson.databind.SerializationFeature
import history.controller.EventControllerDsl.eventController
import history.controller.TodoControllerDsl.todoController
import history.domain.HistoryDomainDsl.historyDomain
import history.domain.InMemoryEventSource
import history.domain.TodoDomainDsl.todoDomain
import io.javalin.Javalin
import io.javalin.json.JavalinJackson

object App {

    fun create(): Javalin {

        val eventSource = InMemoryEventSource
        val eventController = eventController {
            historyDomain(eventSource)
        }
        val todoController = todoController {
            todoDomain { eventSource }
        }

        return Javalin.create { config ->
            with(config) {
                routing.contextPath = "/api"
                jsonMapper(JavalinJackson().updateMapper { mapper ->
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                })
            }
        }.get("/events", eventController::todoIds)
            .get("/events/todos/{id}", eventController::events)
            .get("/todos/{id}", todoController::findTodo)
    }
}