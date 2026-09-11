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

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (title: String, startMinute: Int?, endMinute: Int?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var hasTime by remember { mutableStateOf(false) }
    var hourText by remember { mutableStateOf("9") }
    var minuteText by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
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
                onAdd(title, startMinute, null)
            }) {
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
