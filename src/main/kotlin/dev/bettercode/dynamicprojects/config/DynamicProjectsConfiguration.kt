package dev.bettercode.dynamicprojects.config

import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import dev.bettercode.dynamicprojects.application.*
import dev.bettercode.dynamicprojects.infra.db.inmemory.InMemoryDynamicProjectRepository
import dev.bettercode.dynamicprojects.infra.tasks.TasksClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamicProjectsConfiguration {

    companion object {

        private val inMemoryDynamicProjectRepository = InMemoryDynamicProjectRepository()

        internal fun dynamicProjectsFacade(tasksPort: TasksPort): DynamicProjectsFacade {
            return DynamicProjectsFacade(
                inMemoryDynamicProjectRepository,
                PredefinedDynamicProjectsService(inMemoryDynamicProjectRepository),
                recalculationService = ProjectRecalculationService(inMemoryDynamicProjectRepository, tasksPort = tasksPort)
            )
        }

        internal fun dynamicProjectHandlers(tasksPort: TasksPort, pageSize: Int = 100): DynamicProjectHandlers {
            return DynamicProjectHandlers(
                PredefinedDynamicProjectsService(inMemoryDynamicProjectRepository),
                ProjectRecalculationService(inMemoryDynamicProjectRepository, tasksPort, pageSize = pageSize)
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
            predefinedDynamicProjectsService = predefinedDynamicProjectsService,
            recalculationService = projectRecalculationService
        )
    }

    @Bean
    internal fun predefinedDynamicProjectsService(dynamicProjectRepo: DynamicProjectRepository): PredefinedDynamicProjectsService {
        return PredefinedDynamicProjectsService(dynamicProjectRepo)
    }

    @Bean
    internal fun projectRecalculationService(
        dynamicProjectRepo: DynamicProjectRepository,
        @Value("\${tasks.url}") tasksUrl: String
    ): ProjectRecalculationService {
        return ProjectRecalculationService(dynamicProjectRepo, tasksPort = TasksClient(tasksUrl = tasksUrl))
    }
}