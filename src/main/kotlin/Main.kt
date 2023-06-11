import app.App.Companion.app

fun main() {

    app {
        redis {
            System.getenv("REDIS_HOST")?.let { host = it }
            System.getenv("REDIS_PORT")?.toInt()?.let { port = it }
        }
    }.build()
        .start(7070)
}