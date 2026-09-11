package com.harshiitx.habittickoff.ui.justification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.TaskStatus
import com.harshiitx.habittickoff.data.repository.HabitRepository
import com.harshiitx.habittickoff.data.repository.SettingsRepository
import com.harshiitx.habittickoff.data.repository.TaskRepository
import com.harshiitx.habittickoff.domain.MissedItemDetector
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JustificationViewModel(
    private val habitRepository: HabitRepository,
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val detector = MissedItemDetector(today = { LocalDate.now() })

    private val _pendingQueue = MutableStateFlow<List<PendingJustificationItem>>(emptyList())
    val pendingQueue: StateFlow<List<PendingJustificationItem>> = _pendingQueue.asStateFlow()

    init {
        viewModelScope.launch {
            habitRepository.load()
            taskRepository.load()
            settingsRepository.load()
            runMissedScan()
        }
    }

    private suspend fun runMissedScan() {
        val result = detector.scanAndMarkMissed(
            lastMissedCheckEpochDay = settingsRepository.current.lastMissedCheckEpochDay,
            habits = habitRepository.habits.value,
            logs = habitRepository.logs.value,
            tasks = taskRepository.tasks.value
        )
        result.newHabitLogs.forEach { habitRepository.upsertLog(it) }
        result.updatedTasks.forEach { taskRepository.upsert(it) }
        settingsRepository.update { it.copy(lastMissedCheckEpochDay = result.newLastMissedCheckEpochDay) }
        recomputeQueue()
    }

    private fun recomputeQueue() {
        val habitsById = habitRepository.habits.value.associateBy { it.id }
        val habitItems = habitRepository.logs.value
            .filter { it.status == CompletionStatus.MISSED && it.missedJustification == null }
            .mapNotNull { log ->
                val habit = habitsById[log.habitId] ?: return@mapNotNull null
                PendingJustificationItem.HabitItem(log.habitId, log.epochDay, habit.name)
            }
        val taskItems = taskRepository.tasks.value
            .filter { it.status == TaskStatus.MISSED && it.missedJustification == null }
            .map { PendingJustificationItem.TaskItemPending(it.id, it.epochDay, it.title) }

        _pendingQueue.value = (habitItems + taskItems).sortedBy { it.epochDay }
    }

    fun submitJustification(item: PendingJustificationItem, text: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            when (item) {
                is PendingJustificationItem.HabitItem -> {
                    val log = habitRepository.logFor(item.habitId, item.epochDay) ?: return@launch
                    habitRepository.upsertLog(
                        log.copy(missedJustification = text, justificationSubmittedAtEpochMillis = now)
                    )
                }
                is PendingJustificationItem.TaskItemPending -> {
                    val task = taskRepository.tasks.value.firstOrNull { it.id == item.taskId } ?: return@launch
                    taskRepository.upsert(
                        task.copy(missedJustification = text, justificationSubmittedAtEpochMillis = now)
                    )
                }
            }
            recomputeQueue()
        }
    }
}
