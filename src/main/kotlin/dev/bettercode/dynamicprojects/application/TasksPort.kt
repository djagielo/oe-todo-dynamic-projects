package dev.bettercode.dynamicprojects.application

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TasksPort {
    suspend fun getAllOpen(page: Pageable): Page<TaskDto>
    suspend fun getAllOpen(pageSize: Int): Flow<Page<TaskDto>>
}