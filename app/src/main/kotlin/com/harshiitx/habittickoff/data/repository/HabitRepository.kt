package com.harshiitx.habittickoff.data.repository

import com.harshiitx.habittickoff.data.json.JsonFileStore
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitLog
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

class HabitRepository(dataDir: File) {
    private val habitsStore =
        JsonFileStore(File(dataDir, "habits.json"), ListSerializer(Habit.serializer()))
    private val logsStore =
        JsonFileStore(File(dataDir, "habit_logs.json"), ListSerializer(HabitLog.serializer()))

    val habits: StateFlow<List<Habit>> = habitsStore.items
    val logs: StateFlow<List<HabitLog>> = logsStore.items

    suspend fun load() {
        habitsStore.load()
        logsStore.load()
        if (habitsStore.items.value.isEmpty()) {
            seedSampleHabits()
        }
    }

    private suspend fun seedSampleHabits() {
        val today = LocalDate.now().toEpochDay()
        habitsStore.save(
            listOf(
                Habit(
                    id = "sample-gym",
                    name = "Go To Gym",
                    emoji = "💪",
                    colorHex = "#F2C94C",
                    createdOnEpochDay = today
                ),
                Habit(
                    id = "sample-read",
                    name = "Read 10 Pages",
                    emoji = "📚",
                    colorHex = "#6FCF97",
                    createdOnEpochDay = today
                )
            )
        )
    }

    suspend fun upsertHabit(habit: Habit) {
        val current = habitsStore.items.value.toMutableList()
        val index = current.indexOfFirst { it.id == habit.id }
        if (index >= 0) current[index] = habit else current.add(habit)
        habitsStore.save(current)
    }

    suspend fun archiveHabit(habitId: String) {
        val habit = habitsStore.items.value.firstOrNull { it.id == habitId } ?: return
        upsertHabit(habit.copy(isArchived = true))
    }

    suspend fun upsertLog(log: HabitLog) {
        val current = logsStore.items.value.toMutableList()
        val index = current.indexOfFirst { it.habitId == log.habitId && it.epochDay == log.epochDay }
        if (index >= 0) current[index] = log else current.add(log)
        logsStore.save(current)
    }

    fun logFor(habitId: String, epochDay: Long): HabitLog? =
        logsStore.items.value.firstOrNull { it.habitId == habitId && it.epochDay == epochDay }

    fun logsFor(habitId: String): List<HabitLog> =
        logsStore.items.value.filter { it.habitId == habitId }

    suspend fun markDone(habitId: String, epochDay: Long, backdated: Boolean = false) {
        upsertLog(
            HabitLog(
                habitId = habitId,
                epochDay = epochDay,
                status = CompletionStatus.DONE,
                completedAtEpochMillis = System.currentTimeMillis(),
                backdated = backdated
            )
        )
    }
}
