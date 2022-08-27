package dev.bettercode.dynamicprojects.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TasksPort {
    suspend fun getAllOpen(page: Pageable): Page<TaskDto>
}