package dev.bettercode.dynamicprojects

import java.util.*

data class DynamicProjectId(val id: UUID = UUID.randomUUID()) {
    companion object {
        fun fromString(id: String): DynamicProjectId {
            return DynamicProjectId(id = UUID.fromString(id))
        }
    }
}