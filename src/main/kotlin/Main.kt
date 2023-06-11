import app.App.Companion.app

fun main() {

    val env = System.getenv()
    app {

        val postgresPassword = env["POSTGRES_PASSWORD"]
        // only password is required for Postgres
        if (postgresPassword != null) {
            postgres {
                env["POSTGRES_HOST"]?.let { host = it }
                env["POSTGRES_PORT"]?.toInt()?.let { port = it }
                env["POSTGRES_USERNAME"]?.let { username = it }
                password = postgresPassword
            }
        } else {
            redis {
                env["REDIS_HOST"]?.let { host = it }
                env["REDIS_PORT"]?.toInt()?.let { port = it }
            }
        }
    }.build()
        .start(7070)
}