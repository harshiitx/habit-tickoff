package com.harshiitx.habittickoff.data.model

import kotlinx.serialization.Serializable

enum class ReminderOwnerType { HABIT, TASK }

@Serializable
data class ReminderConfig(
    val id: String,
    val ownerType: ReminderOwnerType,
    val ownerId: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: Set<Int> = (1..7).toSet(),
    /** Set only for a task's one-time reminder; null means a recurring habit reminder. */
    val oneShotEpochDay: Long? = null,
    val enabled: Boolean = true
)
