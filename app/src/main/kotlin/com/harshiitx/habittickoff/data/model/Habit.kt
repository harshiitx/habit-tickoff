package com.harshiitx.habittickoff.data.model

import kotlinx.serialization.Serializable

enum class HabitFrequency { DAILY, SPECIFIC_DAYS }

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val createdOnEpochDay: Long,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val activeDaysOfWeek: Set<Int> = emptySet(),
    val reminderConfigId: String? = null,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0
)

enum class CompletionStatus { DONE, MISSED, NOT_YET_DUE }

@Serializable
data class HabitLog(
    val habitId: String,
    val epochDay: Long,
    val status: CompletionStatus,
    val completedAtEpochMillis: Long? = null,
    val missedJustification: String? = null,
    val justificationSubmittedAtEpochMillis: Long? = null,
    val backdated: Boolean = false
)
