package com.harshiitx.habittickoff.data.model

import kotlinx.serialization.Serializable

enum class TaskStatus { PENDING, DONE, MISSED }

@Serializable
data class TaskItem(
    val id: String,
    val epochDay: Long,
    val title: String,
    val startMinuteOfDay: Int? = null,
    val endMinuteOfDay: Int? = null,
    val categoryColorHex: String = "#8AB4F8",
    val status: TaskStatus = TaskStatus.PENDING,
    val missedJustification: String? = null,
    val justificationSubmittedAtEpochMillis: Long? = null,
    val reminderConfigId: String? = null
)
