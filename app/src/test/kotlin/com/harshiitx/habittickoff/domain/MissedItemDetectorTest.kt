package com.harshiitx.habittickoff.domain

import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitFrequency
import com.harshiitx.habittickoff.data.model.HabitLog
import com.harshiitx.habittickoff.data.model.TaskItem
import com.harshiitx.habittickoff.data.model.TaskStatus
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MissedItemDetectorTest {

    private val fixedToday = LocalDate.of(2026, 9, 11)
    private val todayEpochDay = fixedToday.toEpochDay()
    private val detector = MissedItemDetector(today = { fixedToday })

    private fun dailyHabit(id: String, createdOnEpochDay: Long = todayEpochDay - 100) = Habit(
        id = id,
        name = id,
        emoji = "🔥",
        colorHex = "#000000",
        createdOnEpochDay = createdOnEpochDay
    )

    @Test
    fun `a due habit with no log for yesterday is marked missed`() {
        val habit = dailyHabit("h1")
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 1,
            habits = listOf(habit),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertEquals(1, result.newHabitLogs.size)
        assertEquals(todayEpochDay - 1, result.newHabitLogs.first().epochDay)
        assertEquals(CompletionStatus.MISSED, result.newHabitLogs.first().status)
    }

    @Test
    fun `a habit due today is not flagged until tomorrow's scan`() {
        val habit = dailyHabit("h1")
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay,
            habits = listOf(habit),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertTrue(result.newHabitLogs.isEmpty())
    }

    @Test
    fun `an already-logged day is never overwritten`() {
        val habit = dailyHabit("h1")
        val existingLog = HabitLog(habit.id, todayEpochDay - 1, CompletionStatus.DONE)
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 1,
            habits = listOf(habit),
            logs = listOf(existingLog),
            tasks = emptyList()
        )

        assertTrue(result.newHabitLogs.isEmpty())
    }

    @Test
    fun `an archived habit is never flagged`() {
        val habit = dailyHabit("h1").copy(isArchived = true)
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 1,
            habits = listOf(habit),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertTrue(result.newHabitLogs.isEmpty())
    }

    @Test
    fun `a specific-days habit is only flagged on its active days`() {
        val yesterday = fixedToday.minusDays(1)
        val notYesterdayIso = if (yesterday.dayOfWeek.value == 1) 2 else 1
        val habit = dailyHabit("h1").copy(
            frequency = HabitFrequency.SPECIFIC_DAYS,
            activeDaysOfWeek = setOf(notYesterdayIso)
        )
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 1,
            habits = listOf(habit),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertTrue(result.newHabitLogs.isEmpty())
    }

    @Test
    fun `a pending task from a past day becomes missed`() {
        val task = TaskItem(id = "t1", epochDay = todayEpochDay - 2, title = "Finish report")
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 2,
            habits = emptyList(),
            logs = emptyList(),
            tasks = listOf(task)
        )

        assertEquals(1, result.updatedTasks.size)
        assertEquals(TaskStatus.MISSED, result.updatedTasks.first().status)
    }

    @Test
    fun `a pending task due today is left alone`() {
        val task = TaskItem(id = "t1", epochDay = todayEpochDay, title = "Finish report")
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 2,
            habits = emptyList(),
            logs = emptyList(),
            tasks = listOf(task)
        )

        assertTrue(result.updatedTasks.isEmpty())
    }

    @Test
    fun `lookback clamps to a year so a long-neglected app doesn't loop forever`() {
        val habit = dailyHabit("h1", createdOnEpochDay = todayEpochDay - 10_000)
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 10_000,
            habits = listOf(habit),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertEquals(365, result.newHabitLogs.size)
    }

    @Test
    fun `newLastMissedCheckEpochDay always advances to today`() {
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = todayEpochDay - 5,
            habits = emptyList(),
            logs = emptyList(),
            tasks = emptyList()
        )

        assertEquals(todayEpochDay, result.newLastMissedCheckEpochDay)
    }
}
