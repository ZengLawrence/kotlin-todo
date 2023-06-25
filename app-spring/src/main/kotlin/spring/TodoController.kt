package spring

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)

@RestController
class TodoController {

    @GetMapping("/")
    fun getAll() = listOf(
        TodoDto(1, "Get milk"),
        TodoDto(2, "Prepare Lunch", true),
        TodoDto(3, "Get bread"),
    )
}