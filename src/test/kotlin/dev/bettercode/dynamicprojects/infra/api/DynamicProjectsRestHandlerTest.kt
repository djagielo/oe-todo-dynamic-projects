package dev.bettercode.dynamicprojects.infra.api

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import dev.bettercode.dynamicprojects.DynamicProjectDto
import dev.bettercode.dynamicprojects.DynamicProjectId
import dev.bettercode.dynamicprojects.DynamicProjectsFacade
import dev.bettercode.dynamicprojects.application.TaskId
import dev.bettercode.dynamicprojects.config.DynamicProjectsConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class DynamicProjectsRestHandlerTest {

    private lateinit var handler: DynamicProjectsRestHandler
    private lateinit var client: WebTestClient
    private lateinit var dynamicProjectsFacade: DynamicProjectsFacade

    @BeforeEach
    fun beforeEach() {
        dynamicProjectsFacade = mock()
        handler = DynamicProjectsRestHandler(dynamicProjectsFacade)
        client = WebTestClient.bindToRouterFunction(DynamicProjectsConfiguration().routes(handler)).build()
    }

    @Test
    fun `initialize dynamic projects returns ok`() = runBlocking {
        client.post()
            .uri("/dynamic-projects/initialize")
            .exchange()
            .expectAll({
                it.expectStatus().isOk
                it.expectHeader().contentType(MediaType.APPLICATION_JSON)
            })

        verify(dynamicProjectsFacade).initialize()
    }

    @Test
    fun `return 500 on uncaught exception`() {
        runBlocking {
            `when`(dynamicProjectsFacade.initialize()).thenThrow(RuntimeException())
        }
        client.post()
            .uri("/dynamic-projects/initialize")
            .exchange()
            .expectStatus().is5xxServerError
    }

    @Test
    fun `recalculate all projects returns ok`() = runBlocking {
        client.post()
            .uri("/dynamic-projects/recalculateAll")
            .exchange()
            .expectAll({
                it.expectStatus().isOk
                it.expectHeader().contentType(MediaType.APPLICATION_JSON)
            })

        verify(dynamicProjectsFacade).recalculateAll()
    }

    @Test
    fun `getting list of dynamic projects`() {
        runBlocking {
            `when`(dynamicProjectsFacade.getProjects()).thenReturn(
                listOf(
                    DynamicProjectDto(DynamicProjectId(), "TEST1"),
                    DynamicProjectDto(DynamicProjectId(), "TEST2"),
                    DynamicProjectDto(DynamicProjectId(), "TEST3")
                )
            )
        }
        client.get()
            .uri("/dynamic-projects")
            .exchange()
            .expectAll({
                it.expectStatus().isOk
                it.expectHeader().contentType(MediaType.APPLICATION_JSON)
                it.expectBody().jsonPath("$.content").isArray
                it.expectBody().jsonPath("$.content.length()").isEqualTo(3)
                it.expectBody().jsonPath("$.content[0].name").isEqualTo("TEST1")
                it.expectBody().jsonPath("$.content[1].name").isEqualTo("TEST2")
                it.expectBody().jsonPath("$.content[2].name").isEqualTo("TEST3")
            })
    }

    @Test
    fun `get tasks for a given project`() {
        val projectId = DynamicProjectId()
        val expectedTasks = listOf(TaskId(), TaskId(), TaskId())
        runBlocking {
            `when`(dynamicProjectsFacade.getTasksForAProject(projectId)).thenReturn(
                expectedTasks.toSet()
            )
        }

        client.get()
            .uri("/dynamic-projects/${projectId.id}/tasks")
            .exchange()
            .expectAll({
                it.expectStatus().isOk
                it.expectHeader().contentType(MediaType.APPLICATION_JSON)
                it.expectBody().jsonPath("$.content").isArray
                it.expectBody().jsonPath("$.content.length()").isEqualTo(3)
                it.expectBody().jsonPath("$.content[0].id").isEqualTo(expectedTasks[0].uuid!!)
                it.expectBody().jsonPath("$.content[1].id").isEqualTo(expectedTasks[1].uuid!!)
                it.expectBody().jsonPath("$.content[2].id").isEqualTo(expectedTasks[2].uuid!!)
            })
    }


    @ValueSource(strings = ["lol"])
    @ParameterizedTest
    fun `validate dynamic project id`(id: String) {
        client.get()
            .uri("/dynamic-projects/$id/tasks")
            .exchange()
            .expectAll({
                it.expectStatus().is4xxClientError
            })
    }
}