package com.harshiitx.habittickoff.ui.planner

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.ReminderConfig
import com.harshiitx.habittickoff.data.model.ReminderOwnerType
import com.harshiitx.habittickoff.data.model.TaskItem
import com.harshiitx.habittickoff.data.model.TaskStatus
import com.harshiitx.habittickoff.data.repository.ReminderRepository
import com.harshiitx.habittickoff.data.repository.TaskRepository
import com.harshiitx.habittickoff.reminders.ReminderScheduler
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerViewModel(
    private val repository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val appContext: Context
) : ViewModel() {

    private val _selectedEpochDay = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedEpochDay: StateFlow<Long> = _selectedEpochDay.asStateFlow()

    val tasks: StateFlow<List<TaskItem>> = repository.tasks

    init {
        viewModelScope.launch {
            repository.load()
            reminderRepository.load()
        }
    }

    fun selectDay(epochDay: Long) {
        _selectedEpochDay.value = epochDay
    }

    fun addTask(title: String, startMinuteOfDay: Int?, endMinuteOfDay: Int?, remind: Boolean) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val taskId = UUID.randomUUID().toString()
            val epochDay = _selectedEpochDay.value
            var reminderConfigId: String? = null
            if (remind && startMinuteOfDay != null) {
                val config = ReminderConfig(
                    id = UUID.randomUUID().toString(),
                    ownerType = ReminderOwnerType.TASK,
                    ownerId = taskId,
                    hour = startMinuteOfDay / 60,
                    minute = startMinuteOfDay % 60,
                    oneShotEpochDay = epochDay
                )
                reminderRepository.upsert(config)
                ReminderScheduler.schedule(appContext, config, title.trim())
                reminderConfigId = config.id
            }
            repository.upsert(
                TaskItem(
                    id = taskId,
                    epochDay = epochDay,
                    title = title.trim(),
                    startMinuteOfDay = startMinuteOfDay,
                    endMinuteOfDay = endMinuteOfDay,
                    reminderConfigId = reminderConfigId
                )
            )
        }
    }

    fun toggleDone(task: TaskItem) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.DONE) TaskStatus.PENDING else TaskStatus.DONE
            repository.upsert(task.copy(status = newStatus))
        }
    }

    fun delete(task: TaskItem) {
        viewModelScope.launch {
            task.reminderConfigId?.let { ReminderScheduler.cancel(appContext, it) }
            repository.delete(task.id)
        }
    }
}
