import history.AppBuilder.Companion.app
import history.controller.InMemoryEventSource

fun main() {
    val app = app {
        eventSource = InMemoryEventSource
    }.start(7070)
}

