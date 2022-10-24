package dev.bettercode.dynamicprojects.infra.tasks

import dev.bettercode.dynamicprojects.application.TaskDto
import dev.bettercode.dynamicprojects.application.TasksPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class TasksStub(private var tasks: List<TaskDto>) : TasksPort {

    fun setUp(tasks: List<TaskDto>) {
        this.tasks = tasks
    }

    override suspend fun getAllOpen(page: Pageable): Page<TaskDto> {
        val chunked = tasks.chunked(page.pageSize)
        return if (page.pageNumber >= chunked.size) {
            Page.empty(page)
        } else PageImpl(chunked[page.pageNumber], page, tasks.size.toLong())
    }

    override suspend fun getAllOpen(pageSize: Int): Flow<Page<TaskDto>> = channelFlow {
        var page = 0
        do {
            println("Fetching task page $page")
            val tasks = getAllOpen(PageRequest.of(page, pageSize))
            send(tasks)
            page += 1
        } while (!tasks.isEmpty)
    }
}