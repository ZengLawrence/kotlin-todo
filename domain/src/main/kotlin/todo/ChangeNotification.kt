package todo

import java.lang.System.Logger

interface ChangeNotification {

    fun added(id: Int, description: String)

    fun checkedDone(id: Int)

    fun uncheckedDone(id: Int)

    fun deleted(id: Int)
}

class LoggingChangeNotification(
    private val logger: Logger = System.getLogger(LoggingChangeNotification::class.qualifiedName)
): ChangeNotification {

    override fun added(id: Int, description: String) {
        logger.log(Logger.Level.INFO, "Todo added: id=$id, description=$description")
    }

    override fun checkedDone(id: Int) {
        logger.log(Logger.Level.INFO, "Todo is checked done: id=$id")
    }

    override fun uncheckedDone(id: Int) {
        logger.log(Logger.Level.INFO, "Todo is unchecked done: id=$id")
    }

    override fun deleted(id: Int) {
        logger.log(Logger.Level.INFO, "Todo deleted: id=$id")
    }
}

class IgnoreRuntimeExceptionChangeNotification(private val delegate: ChangeNotification): ChangeNotification {

    private fun ignoreRuntimeError(func: () -> Unit) : Unit = try {
        func()
    } catch (_: RuntimeException) {}

    override fun added(id: Int, description: String) = ignoreRuntimeError {
        delegate.added(id, description)
    }

    override fun checkedDone(id: Int) = ignoreRuntimeError {
        delegate.checkedDone(id)
    }

    override fun uncheckedDone(id: Int) = ignoreRuntimeError {
        delegate.uncheckedDone(id)
    }

    override fun deleted(id: Int)  = ignoreRuntimeError {
        delegate.deleted(id)
    }

}