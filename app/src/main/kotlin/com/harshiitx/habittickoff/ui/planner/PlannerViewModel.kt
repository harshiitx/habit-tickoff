package com.harshiitx.habittickoff.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshiitx.habittickoff.data.model.TaskItem
import com.harshiitx.habittickoff.data.model.TaskStatus
import com.harshiitx.habittickoff.data.repository.TaskRepository
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedEpochDay = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedEpochDay: StateFlow<Long> = _selectedEpochDay.asStateFlow()

    val tasks: StateFlow<List<TaskItem>> = repository.tasks

    init {
        viewModelScope.launch { repository.load() }
    }

    fun selectDay(epochDay: Long) {
        _selectedEpochDay.value = epochDay
    }

    fun addTask(title: String, startMinuteOfDay: Int?, endMinuteOfDay: Int?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.upsert(
                TaskItem(
                    id = UUID.randomUUID().toString(),
                    epochDay = _selectedEpochDay.value,
                    title = title.trim(),
                    startMinuteOfDay = startMinuteOfDay,
                    endMinuteOfDay = endMinuteOfDay
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

    fun delete(taskId: String) {
        viewModelScope.launch { repository.delete(taskId) }
    }
}
