package spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import todo.TodoDomain
import todo.TodoPersistence

@Configuration
class ApplicationConfig {

    @Bean
    fun todoDomain(
        todoPersistence: TodoPersistence,
        todoChangeNotificationService: TodoChangeNotificationService
    ) = TodoDomain(todoPersistence, todoChangeNotificationService)

}