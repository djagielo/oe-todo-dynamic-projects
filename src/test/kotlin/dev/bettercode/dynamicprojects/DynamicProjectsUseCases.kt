package dev.bettercode.dynamicprojects

import dev.bettercode.dynamicprojects.TasksFixtures.Companion.task
import dev.bettercode.dynamicprojects.application.*
import dev.bettercode.dynamicprojects.config.DynamicProjectsConfiguration
import dev.bettercode.dynamicprojects.infra.tasks.TasksStub
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class DynamicProjectsUseCases {

    private val tasksStub: TasksStub = TasksStub(emptyList())

    private val dynamicProjectsFacade: DynamicProjectsFacade =
        DynamicProjectsConfiguration.dynamicProjectsFacade(tasksStub)

    private val dynamicProjectHandlers: DynamicProjectHandlers =
        DynamicProjectsConfiguration.dynamicProjectHandlers(tasksStub)

    @Test
    fun `create predefined dynamic-projects after first project gets created`(): Unit = runBlocking {
        // given - any project gets created
        dynamicProjectHandlers.handleProjectCreated()

        // when
        val dynamicProjects = dynamicProjectsFacade.getProjects()

        // then
        assertThat(dynamicProjects.map { it.name }).containsExactlyInAnyOrder(
            "Completed today", "Today", "Overdue"
        )
    }

    @Test
    fun `create predefined dynamic-projects on initilize`(): Unit = runBlocking {
        // given - any project gets created
        dynamicProjectsFacade.initialize()

        // when
        val dynamicProjects = dynamicProjectsFacade.getProjects()

        // then
        assertThat(dynamicProjects.map { it.name }).containsExactlyInAnyOrder(
            "Completed today", "Today", "Overdue"
        )
    }

    @Test
    fun `recalculate Overdue dynamic project membership`(): Unit = runBlocking {
        // given - any project to generate default ones
        dynamicProjectsFacade.initialize()
        // and - Overdue project created as a result
        val overdueProject = dynamicProjectsFacade.getProjectByName("Overdue")!!

        // and - list of tasks
        val tasks = listOf(
            task(name = "task_due_tomorrow", dueDate = LocalDate.now().plusDays(1)),
            task(name = "task_due_in_2_days", dueDate = LocalDate.now().plusDays(2)),
            task(name = "task_due_in_a_month", dueDate = LocalDate.now().plusDays(30))
        )

        val tasksOverdue = listOf(
            task(name = "task_due_yesterday", dueDate = LocalDate.now().minusDays(1)),
            task(name = "task_due_2_days_ago", dueDate = LocalDate.now().minusDays(2)),
            task(name = "task_due_month_ago", dueDate = LocalDate.now().minusDays(30))
        )

        setupTasks((tasks + tasksOverdue).shuffled())

        // when - Overdue project gets recalculated
        dynamicProjectHandlers.recalculateProject(RecalculateProject(overdueProject.id))

        // then
        assertThat(dynamicProjectsFacade.getTasksForAProject(overdueProject.id)).hasSameElementsAs(
            tasksOverdue.map { it.id }
        )
    }

    @Test
    fun `should recalculate Overdue dynamic project membership when tasks no longer meet conditions`() = runBlocking {
        // given - default projects initialized
        dynamicProjectsFacade.initialize()
        // and - Overdue project created as a result
        val overdueProject = dynamicProjectsFacade.getProjectByName("Overdue")
        assertThat(overdueProject).isNotNull
        overdueProject!!

        // and - list of tasks
        val tasks = listOf(
            task(name = "task_due_tomorrow", dueDate = LocalDate.now().plusDays(1)),
            task(name = "task_due_in_2_days", dueDate = LocalDate.now().plusDays(2)),
            task(name = "task_due_in_a_month", dueDate = LocalDate.now().plusDays(30))
        )

        var tasksOverdue = listOf(
            task(name = "task_due_yesterday", dueDate = LocalDate.now().minusDays(1)),
            task(name = "task_due_2_days_ago", dueDate = LocalDate.now().minusDays(2)),
            task(name = "task_due_month_ago", dueDate = LocalDate.now().minusDays(30))
        )
        setupTasks(tasks + tasksOverdue)

        dynamicProjectHandlers.recalculateProject(RecalculateProject(overdueProject.id))

        // and tasks are added to the dynamic project
        assertThat(dynamicProjectsFacade.getTasksForAProject(overdueProject.id)).hasSameElementsAs(
            tasksOverdue.map { it.id }
        )

        // when - tasks have changed
        tasksOverdue = tasksOverdue.map {
            TaskDto(
                id = it.id,
                name = it.name,
                completionDate = it.completionDate,
                dueDate = LocalDate.now().plus(1, ChronoUnit.MONTHS)
            )
        }
        setupTasks((tasks + tasksOverdue).shuffled())

        // and - project gets recalculated again
        dynamicProjectHandlers.recalculateProject(RecalculateProject(overdueProject.id))

        // then
        assertThat(dynamicProjectsFacade.getTasksForAProject(overdueProject.id)).isEmpty()
    }

    @Test
    fun `recalculate all projects on demand`(): Unit = runBlocking {
        // given a list of tasks
        val tasksCompletedToday = listOf(
            task(name = "task_due_tomorrow", dueDate = LocalDate.now().plusDays(1), Instant.now()),
            task(name = "task_due_in_a_month", dueDate = LocalDate.now().plusDays(30), Instant.now())
        )

        val tasksOverdue = listOf(
            task(name = "task_due_2_days_ago", dueDate = LocalDate.now().minusDays(2)),
            task(name = "task_due_month_ago", dueDate = LocalDate.now().minusDays(30))
        )

        val tasksForToday = listOf(
            task(name = "task for today", dueDate = LocalDate.now())
        )

        setupTasks(tasksCompletedToday + tasksOverdue + tasksForToday)

        // and making assure that default projects are created
        dynamicProjectsFacade.initialize()

        // when
        dynamicProjectHandlers.recalculateAllProjects(RecalculateAllProjects(eventId = UUID.randomUUID().toString()))

        // then

        assertThat(getTasksForProject("Today")).hasSameElementsAs(
            tasksForToday.map { it.id })

        assertThat(getTasksForProject("Overdue")).hasSameElementsAs(
            tasksOverdue.map { it.id })

        assertThat(getTasksForProject("Completed today")).hasSameElementsAs(
            tasksCompletedToday.map { it.id })
    }

    private fun setupTasks(tasks: List<TaskDto>) {
        tasksStub.setUp(tasks)
    }

    private suspend fun getTasksForProject(projectName: String): Set<TaskId> {
        return dynamicProjectsFacade.getTasksForAProject(dynamicProjectsFacade.getProjectByName(projectName)!!.id)
    }
}