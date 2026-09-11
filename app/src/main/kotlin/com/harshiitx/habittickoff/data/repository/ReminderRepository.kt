package com.harshiitx.habittickoff.data.repository

import com.harshiitx.habittickoff.data.json.JsonFileStore
import com.harshiitx.habittickoff.data.model.ReminderConfig
import java.io.File
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

class ReminderRepository(dataDir: File) {
    private val store =
        JsonFileStore(File(dataDir, "reminders.json"), ListSerializer(ReminderConfig.serializer()))

    val reminders: StateFlow<List<ReminderConfig>> = store.items

    suspend fun load() = store.load()

    suspend fun upsert(config: ReminderConfig) {
        val current = store.items.value.toMutableList()
        val index = current.indexOfFirst { it.id == config.id }
        if (index >= 0) current[index] = config else current.add(config)
        store.save(current)
    }

    suspend fun delete(id: String) {
        store.save(store.items.value.filterNot { it.id == id })
    }
}
