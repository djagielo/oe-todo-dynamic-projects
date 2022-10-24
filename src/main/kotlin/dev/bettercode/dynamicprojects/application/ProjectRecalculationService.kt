package dev.bettercode.dynamicprojects.application

import dev.bettercode.dynamicprojects.DynamicProjectId
import kotlinx.coroutines.flow.buffer
import java.time.LocalDate
import java.time.ZoneId
import java.util.function.Predicate

internal class ProjectRecalculationService(
    private val dynamicProjectRepository: DynamicProjectRepository,
    private val tasksPort: TasksPort,
    private val pageSize: Int = 100
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
                    ) == true
                },
                "Today" to Predicate {
                    it.dueDate?.isEqual(
                        LocalDate.now()
                    ) == true
                }
            )

            project.clearTasks()
            val predicate = predicatesMap[project.name]!!

            tasksPort.getAllOpen(pageSize).buffer().collect { taskPage ->
                val filteredTasks = taskPage.content.filter(predicate::test)
                println("Processed page ${taskPage.pageable.pageNumber}")
                project.addTasks(filteredTasks.map { it.id }.toSet())
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
