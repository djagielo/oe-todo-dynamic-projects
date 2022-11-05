package dev.bettercode.dynamicprojects.domain

import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.application.TaskId

class DynamicProject(
    val projectId: DynamicProjectId = DynamicProjectId(),
    val tasks: MutableSet<TaskId> =  mutableSetOf(),
    val name: String,
    val predefined: Boolean = false
) {
    fun addTasks(taskIds: Collection<TaskId>) {
        this.tasks.addAll(taskIds)
    }

    fun clearTasks() {
        this.tasks.clear()
    }
}