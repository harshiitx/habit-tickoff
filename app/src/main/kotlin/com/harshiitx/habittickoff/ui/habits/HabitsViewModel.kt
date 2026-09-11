package com.harshiitx.habittickoff.ui.habits

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitFrequency
import com.harshiitx.habittickoff.data.model.HabitLog
import com.harshiitx.habittickoff.data.model.ReminderConfig
import com.harshiitx.habittickoff.data.model.ReminderOwnerType
import com.harshiitx.habittickoff.data.repository.HabitRepository
import com.harshiitx.habittickoff.data.repository.ReminderRepository
import com.harshiitx.habittickoff.reminders.ReminderScheduler
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitsViewModel(
    private val repository: HabitRepository,
    private val reminderRepository: ReminderRepository,
    private val appContext: Context
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = repository.habits
    val logs: StateFlow<List<HabitLog>> = repository.logs

    init {
        viewModelScope.launch {
            repository.load()
            reminderRepository.load()
        }
    }

    fun addHabit(
        name: String,
        emoji: String,
        colorHex: String,
        frequency: HabitFrequency,
        activeDaysOfWeek: Set<Int>,
        reminderHour: Int?,
        reminderMinute: Int?
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val habitId = UUID.randomUUID().toString()
            var reminderConfigId: String? = null
            if (reminderHour != null && reminderMinute != null) {
                val config = ReminderConfig(
                    id = UUID.randomUUID().toString(),
                    ownerType = ReminderOwnerType.HABIT,
                    ownerId = habitId,
                    hour = reminderHour,
                    minute = reminderMinute,
                    daysOfWeek = if (frequency == HabitFrequency.SPECIFIC_DAYS) activeDaysOfWeek else (1..7).toSet()
                )
                reminderRepository.upsert(config)
                ReminderScheduler.schedule(appContext, config, name.trim())
                reminderConfigId = config.id
            }
            repository.upsertHabit(
                Habit(
                    id = habitId,
                    name = name.trim(),
                    emoji = emoji,
                    colorHex = colorHex,
                    createdOnEpochDay = LocalDate.now().toEpochDay(),
                    frequency = frequency,
                    activeDaysOfWeek = activeDaysOfWeek,
                    reminderConfigId = reminderConfigId
                )
            )
        }
    }

    fun archiveHabit(habitId: String) {
        viewModelScope.launch {
            val habit = habits.value.firstOrNull { it.id == habitId }
            habit?.reminderConfigId?.let { ReminderScheduler.cancel(appContext, it) }
            repository.archiveHabit(habitId)
        }
    }

    /** Tapping the done toggle again clears the entry rather than marking it MISSED. */
    fun toggleDoneToday(habitId: String) {
        val today = LocalDate.now().toEpochDay()
        val alreadyDone = repository.logFor(habitId, today)?.status == CompletionStatus.DONE
        viewModelScope.launch {
            if (alreadyDone) repository.clearLog(habitId, today) else repository.markDone(habitId, today)
        }
    }

    fun setDayStatus(habitId: String, epochDay: Long, done: Boolean) {
        viewModelScope.launch {
            if (done) {
                val today = LocalDate.now().toEpochDay()
                repository.markDone(habitId, epochDay, backdated = epochDay != today)
            } else {
                repository.upsertLog(
                    HabitLog(habitId = habitId, epochDay = epochDay, status = CompletionStatus.MISSED)
                )
            }
        }
    }

    fun clearDay(habitId: String, epochDay: Long) {
        viewModelScope.launch { repository.clearLog(habitId, epochDay) }
    }
}
