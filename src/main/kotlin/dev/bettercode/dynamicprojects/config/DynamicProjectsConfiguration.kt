package dev.bettercode.dynamicprojects.config

import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import dev.bettercode.dynamicprojects.application.*
import dev.bettercode.dynamicprojects.infra.db.inmemory.InMemoryDynamicProjectRepository
import dev.bettercode.dynamicprojects.infra.tasks.TasksClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamicProjectsConfiguration {

    companion object {

        private val inMemoryDynamicProjectRepository = InMemoryDynamicProjectRepository()

        internal fun dynamicProjectsFacade(tasksPort: TasksPort): DynamicProjectsFacade {
            return DynamicProjectsFacade(
                inMemoryDynamicProjectRepository,
                PredefinedDynamicProjectsService(inMemoryDynamicProjectRepository)
            )
        }

        internal fun dynamicProjectHandlers(tasksPort: TasksPort): DynamicProjectHandlers {
            return DynamicProjectHandlers(
                PredefinedDynamicProjectsService(inMemoryDynamicProjectRepository),
                ProjectRecalculationService(inMemoryDynamicProjectRepository, tasksPort)
            )
        }
    }

    @Bean
    internal fun dynamicProjectRepository(): DynamicProjectRepository {
        return InMemoryDynamicProjectRepository()
    }

    @Bean
    internal fun dynamicProjectsFacade(
        dynamicProjectRepo: DynamicProjectRepository,
        predefinedDynamicProjectsService: PredefinedDynamicProjectsService,
        projectRecalculationService: ProjectRecalculationService
    ): DynamicProjectsFacade {
        return DynamicProjectsFacade(
            dynamicProjectsRepo = dynamicProjectRepo,
            predefinedDynamicProjectsService = predefinedDynamicProjectsService
        )
    }

    @Bean
    internal fun predefinedDynamicProjectsService(dynamicProjectRepo: DynamicProjectRepository): PredefinedDynamicProjectsService {
        return PredefinedDynamicProjectsService(dynamicProjectRepo)
    }

    @Bean
    internal fun projectRecalculationService(
        dynamicProjectRepo: DynamicProjectRepository
    ): ProjectRecalculationService {
        return ProjectRecalculationService(dynamicProjectRepo, tasksPort = TasksClient())
    }
}