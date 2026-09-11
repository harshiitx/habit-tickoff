package com.harshiitx.habittickoff.data.repository

import com.harshiitx.habittickoff.data.json.JsonFileStore
import com.harshiitx.habittickoff.data.model.AppSettings
import java.io.File
import kotlinx.serialization.builtins.ListSerializer

/** Settings is a singleton, stored as a 0-or-1-element list for reuse of [JsonFileStore]. */
class SettingsRepository(dataDir: File) {
    private val store =
        JsonFileStore(File(dataDir, "settings.json"), ListSerializer(AppSettings.serializer()))

    val current: AppSettings
        get() = store.items.value.firstOrNull() ?: AppSettings()

    suspend fun load() = store.load()

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        store.save(listOf(transform(current)))
    }
}
