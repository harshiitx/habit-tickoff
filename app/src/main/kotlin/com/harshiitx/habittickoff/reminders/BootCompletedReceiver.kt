package com.harshiitx.habittickoff.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.harshiitx.habittickoff.HabitTickoffApp
import com.harshiitx.habittickoff.data.model.ReminderOwnerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }
        val app = context.applicationContext as HabitTickoffApp
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.reminderRepository.load()
                app.habitRepository.load()
                app.taskRepository.load()
                app.reminderRepository.reminders.value.filter { it.enabled }.forEach { config ->
                    val title = when (config.ownerType) {
                        ReminderOwnerType.HABIT ->
                            app.habitRepository.habits.value.firstOrNull { it.id == config.ownerId }?.name
                        ReminderOwnerType.TASK ->
                            app.taskRepository.tasks.value.firstOrNull { it.id == config.ownerId }?.title
                    } ?: "Habit Tickoff"
                    ReminderScheduler.schedule(context, config, title)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
