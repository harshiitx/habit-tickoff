package com.harshiitx.habittickoff.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitLog
import com.harshiitx.habittickoff.data.repository.HabitRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {

    val habits: StateFlow<List<Habit>> = repository.habits
    val logs: StateFlow<List<HabitLog>> = repository.logs

    init {
        viewModelScope.launch { repository.load() }
    }

    fun markDoneToday(habitId: String) {
        viewModelScope.launch {
            repository.markDone(habitId, LocalDate.now().toEpochDay())
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
}
