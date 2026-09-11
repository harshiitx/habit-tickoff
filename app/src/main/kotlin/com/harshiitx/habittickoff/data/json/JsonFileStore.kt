package com.harshiitx.habittickoff.data.json

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

val AppJson: Json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
}

/**
 * Generic cache-plus-write-through JSON list store. Reads populate an
 * in-memory [StateFlow]; writes go to a temp file and are renamed into
 * place so a crash mid-write can never leave a half-written file behind.
 */
class JsonFileStore<T>(
    private val file: File,
    private val serializer: KSerializer<List<T>>,
    private val json: Json = AppJson
) {
    private val mutex = Mutex()
    private val _items = MutableStateFlow<List<T>>(emptyList())
    val items: StateFlow<List<T>> = _items.asStateFlow()

    suspend fun load() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (file.exists()) {
                runCatching { json.decodeFromString(serializer, file.readText()) }
                    .onSuccess { _items.value = it }
            }
        }
    }

    suspend fun save(newItems: List<T>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            file.parentFile?.mkdirs()
            val tmp = File(file.parentFile, "${file.name}.tmp")
            tmp.writeText(json.encodeToString(serializer, newItems))
            tmp.renameTo(file)
            _items.value = newItems
        }
    }
}
