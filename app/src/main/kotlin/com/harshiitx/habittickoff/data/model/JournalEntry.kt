package com.harshiitx.habittickoff.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val epochDay: Long,
    val text: String,
    val lastEditedEpochMillis: Long
)
