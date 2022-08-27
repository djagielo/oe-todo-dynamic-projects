package dev.bettercode.dynamicprojects.application

import dev.bettercode.dynamicprojects.DynamicProjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.ZoneId
import java.util.function.Predicate

internal class ProjectRecalculationService(
    private val dynamicProjectRepository: DynamicProjectRepository,
    private val tasksQuery: TasksPort
) {
    suspend fun recalculate(dynamicProjectId: DynamicProjectId) {
        dynamicProjectRepository.getProjectById(dynamicProjectId)?.let { project ->
            val predicatesMap = mapOf<String, Predicate<TaskDto>>(
                "Completed today" to Predicate {
                    it.completionDate != null &&
                            LocalDate.ofInstant(it.completionDate, ZoneId.systemDefault()).equals(LocalDate.now())
                },
                "Overdue" to Predicate {
                    it.dueDate?.isBefore(
                        LocalDate.now()
                    )
                },
                "Today" to Predicate {
                    it.dueDate?.isEqual(
                        LocalDate.now()
                    )
                }
            )

            var page: Pageable = PageRequest.of(0, 100)
            var tasks = tasksQuery.getAllOpen(page)
            project.clearTasks()
            while (!tasks.isEmpty) {
                val filteredTasks = tasks.filter(predicatesMap[project.name]!!).toSet()
                project.addTasks(filteredTasks.map { it.id }.toSet())
                page = page.next()
                tasks = tasksQuery.getAllOpen(page)
            }

            dynamicProjectRepository.saveProject(project)
        }

    }

    suspend fun recalculateAll() {
        dynamicProjectRepository.getPredefinedProjects().forEach {
            recalculate(it.projectId)
        }
    }
}
