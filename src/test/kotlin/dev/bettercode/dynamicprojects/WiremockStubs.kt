package dev.bettercode.dynamicprojects

import com.github.tomakehurst.wiremock.client.WireMock.*
import dev.bettercode.dynamicprojects.application.TaskDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WiremockStubs {
    companion object {
        fun stubTasks(tasks: List<TaskDto>) {
            stubFor(get(urlPathEqualTo("/tasks")).withQueryParams(
                mapOf(
                    "page" to equalTo("0"),
                    "size" to equalTo("100")
                )
            ).willReturn(okJson(
                """{
                    "content": 
                        ${
                    tasks.map { task ->
                        """
                                    {
                                        "id": {
                                            "uuid": "${task.id.uuid}"
                                        },
                                        "name": "${task.name}",
                                        "completionDate": ${task.completionDate?.let { instantToDateString(it) }},
                                        "dueDate": ${task.dueDate?.let { "\"${it.format(DateTimeFormatter.ISO_DATE)}\"" } ?: null}
                                    }
                                """.trimIndent()
                    }
                },
                    "number": 0,
                    "size": 100,
                    "totalElements": ${tasks.size},
                    "totalPages": 1
                   }
                """.trimIndent()
            )))

            stubFor(
                get(urlPathEqualTo("/tasks"))
                    .withQueryParams(
                        mapOf(
                            "page" to equalTo("1"),
                            "size" to equalTo("100")
                        )
                    )
                    .willReturn(
                        okJson(
                            """{
                    "content": [],
                    "number": 1,
                    "size": 100,
                    "totalElements": ${tasks.size},
                    "totalPages": 1
                   }
                """.trimIndent()
                        )
                    )
            )
        }

        private fun instantToDateString(it: Instant): String =
            "\"${
                LocalDateTime.ofInstant(it, ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
            }\""
    }
}