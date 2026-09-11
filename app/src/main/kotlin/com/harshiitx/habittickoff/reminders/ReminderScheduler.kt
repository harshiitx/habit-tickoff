package com.harshiitx.habittickoff.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.harshiitx.habittickoff.data.model.ReminderConfig
import java.time.LocalDate
import java.time.ZoneId

const val EXTRA_REMINDER_ID = "reminder_id"
const val EXTRA_TITLE = "title"

object ReminderScheduler {

    fun schedule(context: Context, config: ReminderConfig, title: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!config.enabled) {
            cancel(context, config.id)
            return
        }
        val triggerAt = nextTriggerMillis(config) ?: run {
            cancel(context, config.id)
            return
        }

        val pendingIntent = pendingIntentFor(context, config.id, title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentFor(context, reminderId, title = ""))
    }

    private fun pendingIntentFor(context: Context, reminderId: String, title: String): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Null means "don't schedule" (a one-shot reminder whose day+time has already passed). */
    private fun nextTriggerMillis(config: ReminderConfig): Long? {
        val zone = ZoneId.systemDefault()

        if (config.oneShotEpochDay != null) {
            val dateTime = LocalDate.ofEpochDay(config.oneShotEpochDay).atTime(config.hour, config.minute)
            val millis = dateTime.atZone(zone).toInstant().toEpochMilli()
            return millis.takeIf { it > System.currentTimeMillis() }
        }

        var candidate = java.time.ZonedDateTime.now(zone)
            .withHour(config.hour).withMinute(config.minute).withSecond(0).withNano(0)
        if (!candidate.isAfter(java.time.ZonedDateTime.now(zone))) {
            candidate = candidate.plusDays(1)
        }
        if (config.daysOfWeek.isNotEmpty()) {
            var guard = 0
            while (!config.daysOfWeek.contains(candidate.dayOfWeek.value) && guard < 8) {
                candidate = candidate.plusDays(1)
                guard++
            }
        }
        return candidate.toInstant().toEpochMilli()
    }
}
