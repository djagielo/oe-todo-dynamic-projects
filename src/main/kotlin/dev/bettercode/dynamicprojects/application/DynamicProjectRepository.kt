package dev.bettercode.dynamicprojects.application

import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.domain.DynamicProject

interface DynamicProjectRepository {
    suspend fun getPredefinedProjects(): List<DynamicProject>
    suspend fun getProjectById(id: DynamicProjectId): DynamicProject?
    suspend fun saveProject(project: DynamicProject)
    suspend fun findByName(name: String): DynamicProject?
    suspend fun findTasks(projectId: DynamicProjectId): Set<TaskId>
}