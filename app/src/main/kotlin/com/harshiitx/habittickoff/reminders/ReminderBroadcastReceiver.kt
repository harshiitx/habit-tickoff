package com.harshiitx.habittickoff.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.harshiitx.habittickoff.HabitTickoffApp
import com.harshiitx.habittickoff.data.model.ReminderOwnerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Habit Tickoff"
        NotificationHelper.show(context, reminderId.hashCode(), title, "Tap to open Habit Tickoff")

        val app = context.applicationContext as HabitTickoffApp
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.reminderRepository.load()
                app.habitRepository.load()
                app.taskRepository.load()
                val config = app.reminderRepository.reminders.value.firstOrNull { it.id == reminderId }
                if (config != null && config.enabled && config.oneShotEpochDay == null) {
                    val ownerTitle = when (config.ownerType) {
                        ReminderOwnerType.HABIT ->
                            app.habitRepository.habits.value.firstOrNull { it.id == config.ownerId }?.name
                        ReminderOwnerType.TASK ->
                            app.taskRepository.tasks.value.firstOrNull { it.id == config.ownerId }?.title
                    } ?: title
                    ReminderScheduler.schedule(context, config, ownerTitle)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
