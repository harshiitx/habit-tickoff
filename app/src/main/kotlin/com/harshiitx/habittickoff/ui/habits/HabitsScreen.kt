package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.Habit
import java.time.LocalDate

@Composable
fun HabitsScreen(viewModel: HabitsViewModel) {
    val habits by viewModel.habits.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val today = remember { LocalDate.now().toEpochDay() }

    var pendingBackdate by remember { mutableStateOf<Pair<String, Long>?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var deletingHabit by remember { mutableStateOf<Habit?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(horizontal = 12.dp)) {
            item {
                WeekStrip(
                    habits = habits,
                    logs = logs,
                    today = today,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            items(habits.filterNot { it.isArchived }, key = { it.id }) { habit ->
                val statusByEpochDay = logs
                    .filter { it.habitId == habit.id }
                    .associate { it.epochDay to it.status }

                HabitCard(
                    habit = habit,
                    statusByEpochDay = statusByEpochDay,
                    today = today,
                    onToggleDoneToday = { viewModel.toggleDoneToday(habit.id) },
                    onDayClick = { epochDay -> pendingBackdate = habit.id to epochDay },
                    onEdit = { editingHabit = habit },
                    onDeleteRequest = { deletingHabit = habit }
                )
            }
            item {
                if (habits.isEmpty()) {
                    Text(
                        "No habits yet. Tap + to add your first one.",
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        }
    }

    pendingBackdate?.let { (habitId, epochDay) ->
        BackdateBottomSheet(
            epochDay = epochDay,
            onMarkDone = {
                viewModel.setDayStatus(habitId, epochDay, done = true)
                pendingBackdate = null
            },
            onMarkMissed = {
                viewModel.setDayStatus(habitId, epochDay, done = false)
                pendingBackdate = null
            },
            onClear = {
                viewModel.clearDay(habitId, epochDay)
                pendingBackdate = null
            },
            onDismiss = { pendingBackdate = null }
        )
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onSave = { result ->
                viewModel.addHabit(
                    name = result.name,
                    emoji = result.emoji,
                    colorHex = result.colorHex,
                    frequency = result.frequency,
                    activeDaysOfWeek = result.activeDaysOfWeek,
                    reminderHour = result.reminderHour,
                    reminderMinute = result.reminderMinute
                )
                showAddDialog = false
            }
        )
    }

    editingHabit?.let { habit ->
        AddHabitDialog(
            existing = habit,
            existingReminder = viewModel.reminderConfigFor(habit.reminderConfigId),
            onDismiss = { editingHabit = null },
            onSave = { result ->
                viewModel.updateHabit(
                    habitId = habit.id,
                    name = result.name,
                    emoji = result.emoji,
                    colorHex = result.colorHex,
                    frequency = result.frequency,
                    activeDaysOfWeek = result.activeDaysOfWeek,
                    reminderHour = result.reminderHour,
                    reminderMinute = result.reminderMinute
                )
                editingHabit = null
            }
        )
    }

    deletingHabit?.let { habit ->
        AlertDialog(
            onDismissRequest = { deletingHabit = null },
            title = { Text("Delete \"${habit.name}\"?") },
            text = { Text("This removes it and its whole tracked history. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHabit(habit.id)
                    deletingHabit = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = { TextButton(onClick = { deletingHabit = null }) { Text("Cancel") } }
        )
    }
}
