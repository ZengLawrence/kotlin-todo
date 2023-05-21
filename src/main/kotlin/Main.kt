import app.App.Companion.app

fun main() {

    app {
        redis {
            host("localhost")
            port(6379)
        }
    }.build()
        .start(7070)
}