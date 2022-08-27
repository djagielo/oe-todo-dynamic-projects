package dev.bettercode.dynamicprojects

import dev.bettercode.dynamicprojects.application.TaskDto
import dev.bettercode.dynamicprojects.application.TaskId
import java.time.Instant
import java.time.LocalDate

class TasksFixtures {
    companion object {
        fun task(name: String, dueDate: LocalDate, completionDate: Instant? = null): TaskDto {
            return TaskDto(id = TaskId(), name = name, dueDate = dueDate, completionDate = completionDate)
        }

    }

}
