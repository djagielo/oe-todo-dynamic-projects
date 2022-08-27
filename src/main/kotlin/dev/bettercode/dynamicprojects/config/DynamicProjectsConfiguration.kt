package dev.bettercode.dynamicprojects.config

import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import dev.bettercode.dynamicprojects.application.PredefinedDynamicProjectsService
import dev.bettercode.dynamicprojects.application.DynamicProjectHandlers
import dev.bettercode.dynamicprojects.application.ProjectRecalculationService
import dev.bettercode.dynamicprojects.application.TasksPort
import dev.bettercode.dynamicprojects.infra.db.inmemory.InMemoryDynamicProjectRepository

class DynamicProjectsConfiguration {

    companion object {

        private val inMemoryDynamicProjectRepository = InMemoryDynamicProjectRepository()

        internal fun dynamicProjectsFacade(): DynamicProjectsFacade {
            return DynamicProjectsFacade(inMemoryDynamicProjectRepository)
        }

        internal fun dynamicProjectHandlers(tasksPort: TasksPort): DynamicProjectHandlers {
            return DynamicProjectHandlers(
                PredefinedDynamicProjectsService(inMemoryDynamicProjectRepository),
                ProjectRecalculationService(inMemoryDynamicProjectRepository, tasksPort)
            )
        }
    }
}