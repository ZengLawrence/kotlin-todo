import App.Companion.app

fun main() {

    app {
        db {
            host("localhost")
            port(6379)
        }
    }.build()
        .start(7070)
}