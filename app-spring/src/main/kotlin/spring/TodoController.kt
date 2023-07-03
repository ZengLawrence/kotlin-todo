package spring

import arrow.core.Either
import arrow.core.separateEither
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import todo.EmptyTodoDescription
import todo.Todo
import todo.TodoDomain
import todo.TooLongDescription

data class TodoDto(val id: Int, val description: String, val done: Boolean = false)

data class NewTodoDto(val description: String)

data class IdDto(val id: Int)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PatchTodoDto(val done: Boolean?)

data class ErrorDto(val errorDescription: String)

private fun Todo.toDto(): TodoDto {
    return TodoDto(this.id, this.description, this.done)
}
@RestController
class TodoController(@Autowired val todoDomain: TodoDomain) {

    @GetMapping("/todos")
    fun getAll() = todoDomain.findAll()
        .map { it.map(Todo::toDto) }
        .separateEither()
        .second

    @GetMapping("/todos/{id}")
    fun get(@PathVariable("id") id: Int): ResponseEntity<*> {
        val todo = todoDomain.find(id)
            ?.map(Todo::toDto)
        return when (todo) {
            is Either.Right -> ResponseEntity.ok(todo.value)
            is Either.Left -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build<Unit>()
            else -> ResponseEntity.notFound().build<Unit>()
        }
    }

    @PostMapping("/todos")
    fun create(@RequestBody newTodoDto: NewTodoDto): ResponseEntity<*> {
        val id = todoDomain.add(newTodoDto.description)
        return when (id) {
            is Either.Right -> ResponseEntity.status(HttpStatus.CREATED).body(IdDto(id.value))

            is Either.Left -> when (id.value) {
                is EmptyTodoDescription -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorDto("'description' attribute can not be empty"))
                is TooLongDescription -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorDto("'description' attribute can not be longer than 100 characters"))
            }
        }
    }

    @PatchMapping("/todos/{id}")
    fun update(
        @PathVariable("id") id: Int,
        @RequestBody patchTodoDto: PatchTodoDto
    ): ResponseEntity<*> =
        if (patchTodoDto.done != null) {
            todoDomain.toggleDone(id, patchTodoDto.done)
            ResponseEntity.noContent().build<Unit>()
        } else {
            ResponseEntity.badRequest()
                .body(ErrorDto("'done' attribute is not provided"))
        }

    @DeleteMapping("/todos/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Unit> {
        todoDomain.delete(id)
        return ResponseEntity.noContent().build()
    }
}