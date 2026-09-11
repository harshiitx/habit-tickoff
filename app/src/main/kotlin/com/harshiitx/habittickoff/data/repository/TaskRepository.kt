package com.harshiitx.habittickoff.data.repository

import com.harshiitx.habittickoff.data.json.JsonFileStore
import com.harshiitx.habittickoff.data.model.TaskItem
import java.io.File
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

class TaskRepository(dataDir: File) {
    private val store =
        JsonFileStore(File(dataDir, "tasks.json"), ListSerializer(TaskItem.serializer()))

    val tasks: StateFlow<List<TaskItem>> = store.items

    suspend fun load() = store.load()

    fun tasksFor(epochDay: Long): List<TaskItem> =
        store.items.value.filter { it.epochDay == epochDay }

    suspend fun upsert(task: TaskItem) {
        val current = store.items.value.toMutableList()
        val index = current.indexOfFirst { it.id == task.id }
        if (index >= 0) current[index] = task else current.add(task)
        store.save(current)
    }

    suspend fun delete(taskId: String) {
        store.save(store.items.value.filterNot { it.id == taskId })
    }
}
