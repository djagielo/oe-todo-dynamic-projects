package dev.bettercode.dynamicprojects.infra.db.inmemory

import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.application.DynamicProjectRepository
import dev.bettercode.dynamicprojects.application.TaskId
import dev.bettercode.dynamicprojects.domain.DynamicProject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryDynamicProjectRepository : DynamicProjectRepository {
    private val db = ConcurrentHashMap<UUID, DynamicProject>()

    override suspend fun getPredefinedProjects(): List<DynamicProject> {
        return db.values.filter { it.predefined }.toList()
    }

    override suspend fun getProjectById(projectId: DynamicProjectId): DynamicProject? {
        return db[projectId.id]
    }

    override suspend fun saveProject(project: DynamicProject) {
        db[project.projectId.id] = project
    }

    override suspend fun findByName(name: String): DynamicProject? {
        return db.values.firstOrNull {
            it.name.contains(name)
        }
    }

    override suspend fun findTasks(projectId: DynamicProjectId): Set<TaskId> {
        return db[projectId.id]?.tasks ?: emptySet()
    }
}