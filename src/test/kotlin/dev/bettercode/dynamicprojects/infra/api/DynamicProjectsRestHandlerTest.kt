package dev.bettercode.dynamicprojects.infra.api

import dev.bettercode.dynamicprojects.config.DynamicProjectsConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient

class DynamicProjectsRestHandlerTest {

    private lateinit var handler: DynamicProjectsRestHandler
    private lateinit var client: WebTestClient

    @BeforeEach
    fun beforeEach() {
        handler = DynamicProjectsRestHandler()
        client = WebTestClient.bindToRouterFunction(DynamicProjectsConfiguration().routes(handler)).build()
    }

    @Test
    fun `initialize dynamic projects returns ok`() {
        // when
        client.post()
            .uri("/dynamic-projects/initialize")
            .exchange()
            .expectAll({
                it.expectStatus().isOk
            })

    }
}