package dev.bettercode.dynamicprojects

import dev.bettercode.dynamicprojects.domain.DynamicProject

data class DynamicProjectDto(
    val id: DynamicProjectId,
    val name: String
) {
    companion object {
        fun from(it: DynamicProject): DynamicProjectDto {
            return DynamicProjectDto(id = it.projectId, name = it.name)
        }
    }
}