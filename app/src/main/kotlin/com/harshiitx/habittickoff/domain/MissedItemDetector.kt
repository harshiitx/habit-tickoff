package com.harshiitx.habittickoff.domain

import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitFrequency
import com.harshiitx.habittickoff.data.model.HabitLog
import com.harshiitx.habittickoff.data.model.TaskItem
import com.harshiitx.habittickoff.data.model.TaskStatus
import java.time.LocalDate

private const val MAX_LOOKBACK_DAYS = 365L

data class MissedScanResult(
    val newHabitLogs: List<HabitLog>,
    val updatedTasks: List<TaskItem>,
    val newLastMissedCheckEpochDay: Long
)

fun isHabitDueOn(habit: Habit, epochDay: Long): Boolean {
    if (habit.isArchived) return false
    if (epochDay < habit.createdOnEpochDay) return false
    return when (habit.frequency) {
        HabitFrequency.DAILY -> true
        HabitFrequency.SPECIFIC_DAYS -> {
            val isoDayOfWeek = LocalDate.ofEpochDay(epochDay).dayOfWeek.value
            habit.activeDaysOfWeek.contains(isoDayOfWeek)
        }
    }
}

/**
 * Sweeps every *strictly past* day since the last check and flags any due
 * habit with no log, or any still-pending task from a past day, as MISSED.
 * A habit due today is never auto-flagged until tomorrow's scan. Pure
 * Kotlin, no Android imports, so it's directly unit-testable with a fixed
 * clock.
 */
class MissedItemDetector(private val today: () -> LocalDate) {

    fun scanAndMarkMissed(
        lastMissedCheckEpochDay: Long,
        habits: List<Habit>,
        logs: List<HabitLog>,
        tasks: List<TaskItem>
    ): MissedScanResult {
        val todayEpochDay = today().toEpochDay()
        val lookbackStart = maxOf(lastMissedCheckEpochDay, todayEpochDay - MAX_LOOKBACK_DAYS)

        val newHabitLogs = mutableListOf<HabitLog>()
        if (lookbackStart < todayEpochDay) {
            val existingLogDays = logs.map { it.habitId to it.epochDay }.toSet()
            for (epochDay in lookbackStart until todayEpochDay) {
                for (habit in habits) {
                    if (!isHabitDueOn(habit, epochDay)) continue
                    if ((habit.id to epochDay) in existingLogDays) continue
                    newHabitLogs.add(
                        HabitLog(habitId = habit.id, epochDay = epochDay, status = CompletionStatus.MISSED)
                    )
                }
            }
        }

        val updatedTasks = tasks
            .filter { it.status == TaskStatus.PENDING && it.epochDay < todayEpochDay }
            .map { it.copy(status = TaskStatus.MISSED) }

        return MissedScanResult(
            newHabitLogs = newHabitLogs,
            updatedTasks = updatedTasks,
            newLastMissedCheckEpochDay = todayEpochDay
        )
    }
}
