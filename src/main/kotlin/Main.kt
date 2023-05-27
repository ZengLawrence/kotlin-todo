import app.App.Companion.app

fun main() {

    app {
        redis {
            host(System.getenv("REDIS_HOST") ?: "localhost")
            port(System.getenv("REDIS_PORT")?.toInt() ?: 6379)
        }
    }.build()
        .start(7070)
}