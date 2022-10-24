package dev.bettercode.dynamicprojects.application

import dev.bettercode.dynamicprojects.domain.DynamicProject
import java.time.LocalDate
import java.util.function.Predicate

class PredefinedDynamicProjectsService(private val dynamicProjectRepository: DynamicProjectRepository) {

    suspend fun createPredefined() {
        val defaultProjects = dynamicProjectRepository.getPredefinedProjects()

        val predicatesMap = mapOf<String, Predicate<TaskDto>>(
            "Completed today" to Predicate {
                it.completionDate?.equals(
                    LocalDate.now()
                ) == true
            },
            "Overdue" to Predicate {
                it.dueDate?.isBefore(
                    LocalDate.now()
                ) == true
            },
            "Today" to Predicate {
                it.dueDate?.isEqual(
                    LocalDate.now()
                ) == true
            }
        )

        if (defaultProjects.isEmpty()) {
            predicatesMap.forEach {
                dynamicProjectRepository.saveProject(
                    DynamicProject(
                        name = it.key,
                        predefined = true
                    )
                )
            }
        }
    }
}