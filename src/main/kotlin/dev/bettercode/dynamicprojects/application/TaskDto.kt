package dev.bettercode.dynamicprojects.application

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TaskId(val uuid: String? = UUID.randomUUID().toString())

data class TaskDto(
    val id: TaskId,
    val name: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    val completionDate: Instant? = null,
    val dueDate: LocalDate? = null
)