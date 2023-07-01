package spring

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository


@Table("task")
data class Task(
    @Id @Column("task_id") var id: Int?,
    val description: String,
    val done: Boolean,
    )

interface TodoRepository: CrudRepository<Task, Int>
