package persistence.exposed

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TTasks: IntIdTable("task", columnName = "task_id") {
    val description = varchar("description", 100)
    val done = bool("done")
}

class TTask(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TTask>(TTasks)

    var description by TTasks.description
    var done by TTasks.done
}