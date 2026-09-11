package com.harshiitx.habittickoff.ui.planner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshiitx.habittickoff.data.model.TaskItem
import com.harshiitx.habittickoff.data.model.TaskStatus
import com.harshiitx.habittickoff.ui.common.DatePickerButton
import com.harshiitx.habittickoff.ui.common.ruledPaperBackground
import com.harshiitx.habittickoff.ui.theme.HandwrittenFontFamily
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private fun minuteOfDayLabel(minute: Int): String = "%02d:%02d".format(minute / 60, minute % 60)

@Composable
fun PlannerScreen(viewModel: PlannerViewModel) {
    val selectedEpochDay by viewModel.selectedEpochDay.collectAsState()
    val allTasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }
    val todayEpochDay = remember { LocalDate.now().toEpochDay() }

    val dateLabel = remember(selectedEpochDay) {
        LocalDate.ofEpochDay(selectedEpochDay).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    }
    val dayTasks = remember(allTasks, selectedEpochDay) {
        allTasks
            .filter { it.epochDay == selectedEpochDay }
            .sortedWith(compareBy(nullsLast()) { it.startMinuteOfDay })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().ruledPaperBackground()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.selectDay(selectedEpochDay - 1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        dateLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = HandwrittenFontFamily),
                        color = Color(0xFF2B2B2B)
                    )
                    DatePickerButton(selectedEpochDay = selectedEpochDay, onDateSelected = viewModel::selectDay)
                }
                IconButton(onClick = { viewModel.selectDay(selectedEpochDay + 1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next day")
                }
            }
            if (selectedEpochDay != todayEpochDay) {
                TextButton(
                    onClick = { viewModel.selectDay(todayEpochDay) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Jump to Today", fontFamily = HandwrittenFontFamily)
                }
            }
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                if (dayTasks.isEmpty()) {
                    item {
                        Text(
                            "No tasks for this day. Tap + to add one.",
                            color = Color(0xFF2B2B2B),
                            fontFamily = HandwrittenFontFamily,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
                items(dayTasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggleDone = { viewModel.toggleDone(task) },
                        onDelete = { viewModel.delete(task) },
                        onEdit = { editingTask = task }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, startMinute, endMinute, remind ->
                viewModel.addTask(title, startMinute, endMinute, remind)
                showAddDialog = false
            }
        )
    }

    editingTask?.let { task ->
        AddTaskDialog(
            existing = task,
            existingReminder = viewModel.reminderConfigFor(task.reminderConfigId),
            onDismiss = { editingTask = null },
            onSave = { title, startMinute, endMinute, remind ->
                viewModel.updateTask(task, title, startMinute, endMinute, remind)
                editingTask = null
            }
        )
    }
}

@Composable
private fun TaskRow(task: TaskItem, onToggleDone: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.status == TaskStatus.DONE, onCheckedChange = { onToggleDone() })
        task.startMinuteOfDay?.let {
            Text(
                minuteOfDayLabel(it),
                modifier = Modifier.width(56.dp),
                color = Color(0xFF2B2B2B),
                fontFamily = HandwrittenFontFamily
            )
        }
        Text(
            text = task.title,
            modifier = Modifier.weight(1f).clickable(onClick = onEdit),
            color = Color(0xFF2B2B2B),
            fontFamily = HandwrittenFontFamily,
            fontSize = 18.sp,
            textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else null
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete task")
        }
    }
}
