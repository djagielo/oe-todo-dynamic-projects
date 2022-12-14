package dev.bettercode.dynamicprojects.infra.tasks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.bettercode.dynamicprojects.application.TaskDto
import dev.bettercode.dynamicprojects.application.TasksPort
import dev.bettercode.dynamicprojects.config.TasksClientProperties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient


@JsonIgnoreProperties(ignoreUnknown = true)
private data class TasksList(val content: List<TaskDto>, val totalElements: Long)

class TasksClient(private val webClient: WebClient = WebClient.create(), private val tasksClientProperties: TasksClientProperties): TasksPort {

    override suspend fun getAllOpen(page: Pageable): Page<TaskDto> {
        return fetchPage(page.pageNumber, page.pageSize)
    }

    private suspend fun fetchPage(pageNo: Int, pageSize: Int): Page<TaskDto> {
        return webClient.get().uri("${tasksClientProperties.tasksClientUrl}?page=${pageNo}&size=${pageSize}")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(TasksList::class.java).map {
                PageImpl(it.content, PageRequest.of(pageNo, pageSize), it.totalElements)
            }.timeout(tasksClientProperties.tasksClientTimeout).awaitSingle()
    }

    override suspend fun getAllOpen(pageSize: Int): Flow<Page<TaskDto>> = channelFlow {
        var page = 0
        do {
            val tasks = fetchPage(page++, pageSize)
            send(tasks)
        }while (!tasks.isEmpty)
    }
}