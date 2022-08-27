package dev.bettercode.dynamicprojects.application

internal class DynamicProjectHandlers(
    private val predefinedDynamicProjectsService: PredefinedDynamicProjectsService,
    private val projectRecalculationService: ProjectRecalculationService
) {
    suspend fun handleProjectCreated() {
        this.predefinedDynamicProjectsService.createPredefined()
    }

    suspend fun recalculateProject(event: RecalculateProject) {
        this.projectRecalculationService.recalculate(event.dynamicProjectId)
    }

    suspend fun recalculateAllProjects(event: RecalculateAllProjects) {
        this.projectRecalculationService.recalculateAll()
    }
}