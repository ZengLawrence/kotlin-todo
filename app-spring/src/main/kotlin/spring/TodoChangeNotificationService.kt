package spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import todo.ChangeNotification

@Service
class TodoChangeNotificationService(
    @Autowired val kafkaTemplate: KafkaTemplate<String, String>
): ChangeNotification {
    override fun added(id: Int, description: String) {
        kafkaTemplate.send("todo_change_notification", "{id:$id,description:\"$description\",op:\"ADD\"}")
    }

    override fun checkedDone(id: Int) {
        kafkaTemplate.send("todo_change_notification", "{id:$id,op:\"CHECKED_DONE\"}")
    }

    override fun uncheckedDone(id: Int) {
        kafkaTemplate.send("todo_change_notification", "{id:$id,op:\"UNCHECKED_DONE\"}")
    }

    override fun deleted(id: Int) {
        kafkaTemplate.send("todo_change_notification", "{id:$id,op:\"DELETED\"}")
    }
}