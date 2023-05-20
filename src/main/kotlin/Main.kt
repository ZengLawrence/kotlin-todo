fun main() {
    App.create(AppConfig(DBConfig("localhost", 6379))).start(7070)
}