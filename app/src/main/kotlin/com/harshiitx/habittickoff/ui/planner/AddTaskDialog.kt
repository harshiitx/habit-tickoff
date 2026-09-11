package com.harshiitx.habittickoff.ui.planner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.ReminderConfig
import com.harshiitx.habittickoff.data.model.TaskItem

@Composable
fun AddTaskDialog(
    existing: TaskItem? = null,
    existingReminder: ReminderConfig? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, startMinute: Int?, endMinute: Int?, remind: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var hasTime by remember { mutableStateOf(existing?.startMinuteOfDay != null) }
    var hourText by remember {
        mutableStateOf(((existing?.startMinuteOfDay ?: 9 * 60) / 60).toString())
    }
    var minuteText by remember {
        mutableStateOf(((existing?.startMinuteOfDay ?: 0) % 60).toString())
    }
    var remind by remember { mutableStateOf(existingReminder != null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing != null) "Edit task" else "New task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasTime, onCheckedChange = { hasTime = it })
                    Text("Specific time")
                }
                if (hasTime) {
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { hourText = it.filter(Char::isDigit).take(2) },
                            label = { Text("HH") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp)
                        )
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { minuteText = it.filter(Char::isDigit).take(2) },
                            label = { Text("MM") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(start = 8.dp).width(80.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = remind, onCheckedChange = { remind = it })
                        Text("Remind me")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val startMinute = if (hasTime) {
                    val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0
                    val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                    hour * 60 + minute
                } else {
                    null
                }
                onSave(title, startMinute, null, hasTime && remind)
            }) {
                Text(if (existing != null) "Save" else "Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
