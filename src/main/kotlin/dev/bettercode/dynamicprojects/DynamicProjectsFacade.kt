package dev.bettercode.dynamicprojects

import dev.bettercode.dynamicprojects.application.DynamicProjectRepository
import dev.bettercode.dynamicprojects.application.PredefinedDynamicProjectsService
import dev.bettercode.dynamicprojects.application.ProjectRecalculationService
import dev.bettercode.dynamicprojects.application.TaskId


internal open class DynamicProjectsFacade(
    private val dynamicProjectsRepo: DynamicProjectRepository,
    private val predefinedDynamicProjectsService: PredefinedDynamicProjectsService,
    private val recalculationService: ProjectRecalculationService
) {
    open suspend fun getProjects(): List<DynamicProjectDto> {
        return dynamicProjectsRepo.getPredefinedProjects().map {
            DynamicProjectDto.from(it)
        }.toList()
    }

    suspend fun getProjectByName(projectName: String): DynamicProjectDto? {
        return dynamicProjectsRepo.findByName(name = projectName)?.let {
            DynamicProjectDto.from(it)
        }
    }

    open suspend fun getTasksForAProject(projectId: DynamicProjectId): Set<TaskId> {
        return dynamicProjectsRepo.findTasks(projectId)
    }

    open suspend fun initialize() {
        predefinedDynamicProjectsService.createPredefined()
    }

    open suspend fun recalculateAll() {
        recalculationService.recalculateAll()
    }

}