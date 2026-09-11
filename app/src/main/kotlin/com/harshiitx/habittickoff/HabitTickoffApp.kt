package com.harshiitx.habittickoff

import android.app.Application
import com.harshiitx.habittickoff.data.repository.HabitRepository
import com.harshiitx.habittickoff.data.repository.JournalRepository
import com.harshiitx.habittickoff.data.repository.ReminderRepository
import com.harshiitx.habittickoff.data.repository.SettingsRepository
import com.harshiitx.habittickoff.data.repository.TaskRepository
import java.io.File

class HabitTickoffApp : Application() {

    private val habitDataDir: File by lazy { File(filesDir, "habitdata").apply { mkdirs() } }

    val habitRepository: HabitRepository by lazy { HabitRepository(habitDataDir) }
    val taskRepository: TaskRepository by lazy { TaskRepository(habitDataDir) }
    val journalRepository: JournalRepository by lazy { JournalRepository(habitDataDir) }
    val reminderRepository: ReminderRepository by lazy { ReminderRepository(habitDataDir) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(habitDataDir) }
}
