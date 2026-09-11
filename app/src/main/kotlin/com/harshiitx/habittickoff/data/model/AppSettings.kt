package com.harshiitx.habittickoff.data.model

import kotlinx.serialization.Serializable

const val SCHEMA_VERSION = 1

@Serializable
data class AppSettings(
    val schemaVersion: Int = SCHEMA_VERSION,
    val lastMissedCheckEpochDay: Long = 0,
    val lastQuoteIndex: Int = -1,
    val exactAlarmPermissionRequested: Boolean = false
)

@Serializable
data class BackupPayload(
    val schemaVersion: Int = SCHEMA_VERSION,
    val exportedAtEpochMillis: Long,
    val habits: List<Habit>,
    val habitLogs: List<HabitLog>,
    val tasks: List<TaskItem>,
    val journalEntries: List<JournalEntry>,
    val reminderConfigs: List<ReminderConfig>,
    val settings: AppSettings
)
