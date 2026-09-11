package com.harshiitx.habittickoff.ui.justification

sealed class PendingJustificationItem {
    abstract val epochDay: Long
    abstract val title: String

    data class HabitItem(
        val habitId: String,
        override val epochDay: Long,
        override val title: String
    ) : PendingJustificationItem()

    data class TaskItemPending(
        val taskId: String,
        override val epochDay: Long,
        override val title: String
    ) : PendingJustificationItem()
}
