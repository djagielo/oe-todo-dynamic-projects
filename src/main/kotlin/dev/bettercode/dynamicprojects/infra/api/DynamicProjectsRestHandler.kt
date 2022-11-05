package dev.bettercode.dynamicprojects.infra.api

import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.*

internal class DynamicProjectsRestHandler(private val dynamicProjectsFacade: DynamicProjectsFacade) {
    suspend fun getList(request: ServerRequest): ServerResponse {
        val projects = dynamicProjectsFacade.getProjects()
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(PageImpl(projects))
    }

    suspend fun getTasks(request: ServerRequest): ServerResponse {
        val tasks =
            dynamicProjectsFacade.getTasksForAProject(DynamicProjectId(id = UUID.fromString(request.pathVariable("projectId"))))
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(PageImpl(tasks.toList().map {
            mapOf("id" to it.uuid)
        }))
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