package dev.bettercode.dynamicprojects

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.bettercode.dynamicprojects.application.TaskId
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("it")
@WireMockTest(httpPort = 9999)
class DynamicProjectsIntegrationTests {

    @Autowired
    private lateinit var dynamicProjectsFacade: DynamicProjectsFacade

    @Test
    fun `initialize and recalculate all projects`(): Unit = runBlocking {
        // given a list of tasks
        val tasksCompletedToday = listOf(
            TasksFixtures.task(name = "task_due_tomorrow", dueDate = LocalDate.now().plusDays(1), Instant.now()),
            TasksFixtures.task(name = "task_due_in_a_month", dueDate = LocalDate.now().plusDays(30), Instant.now())
        )

        val tasksOverdue = listOf(
            TasksFixtures.task(name = "task_due_2_days_ago", dueDate = LocalDate.now().minusDays(2)),
            TasksFixtures.task(name = "task_due_month_ago", dueDate = LocalDate.now().minusDays(30))
        )

        val tasksForToday = listOf(
            TasksFixtures.task(name = "task for today", dueDate = LocalDate.now())
        )

        val tasks = tasksCompletedToday + tasksOverdue + tasksForToday

        WiremockStubs.stubTasks((tasks))


        // when
        dynamicProjectsFacade.initialize()
        dynamicProjectsFacade.recalculateAll()

        // then
        Assertions.assertThat(getTasksForProject("Today")).hasSameElementsAs(
            tasksForToday.map { it.id })

        Assertions.assertThat(getTasksForProject("Overdue")).hasSameElementsAs(
            tasksOverdue.map { it.id })

        Assertions.assertThat(getTasksForProject("Completed today")).hasSameElementsAs(
            tasksCompletedToday.map { it.id })
    }

    private suspend fun getTasksForProject(projectName: String): Set<TaskId> {
        return dynamicProjectsFacade.getTasksForAProject(dynamicProjectsFacade.getProjectByName(projectName)!!.id)
    }
}