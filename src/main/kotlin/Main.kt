import app.App.Companion.app

fun main() {

    val env = System.getenv()
    app {
        redis {
            env["REDIS_HOST"]?.let { host = it }
            env["REDIS_PORT"]?.toInt()?.let { port = it }
        }
    }.build()
        .start(7070)
}