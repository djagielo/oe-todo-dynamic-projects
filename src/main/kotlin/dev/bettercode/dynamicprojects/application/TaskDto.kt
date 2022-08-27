package dev.bettercode.dynamicprojects.application

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TaskId(val uuid: String? = UUID.randomUUID().toString())

data class TaskDto(val id: TaskId, val name: String, val completionDate: Instant? = null, val dueDate: LocalDate)