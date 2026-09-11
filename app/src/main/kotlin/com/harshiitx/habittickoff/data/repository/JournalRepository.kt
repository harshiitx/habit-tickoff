package com.harshiitx.habittickoff.data.repository

import com.harshiitx.habittickoff.data.json.JsonFileStore
import com.harshiitx.habittickoff.data.model.JournalEntry
import java.io.File
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

class JournalRepository(dataDir: File) {
    private val store =
        JsonFileStore(File(dataDir, "journal.json"), ListSerializer(JournalEntry.serializer()))

    val entries: StateFlow<List<JournalEntry>> = store.items

    suspend fun load() = store.load()

    fun entryFor(epochDay: Long): JournalEntry? =
        store.items.value.firstOrNull { it.epochDay == epochDay }

    suspend fun saveEntry(epochDay: Long, text: String) {
        val current = store.items.value.toMutableList()
        val index = current.indexOfFirst { it.epochDay == epochDay }
        val entry = JournalEntry(epochDay, text, System.currentTimeMillis())
        if (index >= 0) current[index] = entry else current.add(entry)
        store.save(current)
    }
}
