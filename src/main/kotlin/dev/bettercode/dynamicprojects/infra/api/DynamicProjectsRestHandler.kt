package dev.bettercode.dynamicprojects.infra.api

import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

internal class DynamicProjectsRestHandler(private val dynamicProjectsFacade: DynamicProjectsFacade) {
    suspend fun getList(request: ServerRequest): ServerResponse {
        val projects = dynamicProjectsFacade.getProjects()
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(PageImpl(projects))
    }

    suspend fun getTasks(request: ServerRequest): ServerResponse {
        return try {
            val dynamicProjectId = DynamicProjectId.fromString(request.pathVariable("projectId"))

            val tasks = dynamicProjectsFacade.getTasksForAProject(dynamicProjectId).map {
                mapOf("id" to it.uuid)
            }.toList()

            ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(PageImpl(tasks))
        } catch (ex: IllegalArgumentException) {
            badRequest().bodyValueAndAwait("")
        }
    }

    suspend fun initalize(request: ServerRequest): ServerResponse {
        dynamicProjectsFacade.initialize()
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait("")
    }

    suspend fun recalculateAll(request: ServerRequest): ServerResponse {
        dynamicProjectsFacade.recalculateAll()
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait("")
    }
}