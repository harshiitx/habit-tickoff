package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.HabitFrequency

private val EMOJI_CHOICES = listOf("💪", "📚", "🧘", "🎨", "☀️", "💧", "🎯", "📝", "🏃", "🎸")
private val COLOR_CHOICES = listOf(
    "#F2C94C", "#6FCF97", "#EB5757", "#56CCF2", "#BB6BD9", "#F2994A"
)

@Composable
fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (AddHabitResult) -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(EMOJI_CHOICES.first()) }
    var colorHex by remember { mutableStateOf(COLOR_CHOICES.first()) }
    var hasReminder by remember { mutableStateOf(false) }
    var hourText by remember { mutableStateOf("8") }
    var minuteText by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New habit") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                Text("Icon", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    EMOJI_CHOICES.forEach { choice ->
                        EmojiChip(choice, selected = choice == emoji, onClick = { emoji = choice })
                    }
                }
                Text("Color", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    COLOR_CHOICES.forEach { hex ->
                        ColorSwatch(hex, selected = hex == colorHex, onClick = { colorHex = hex })
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasReminder, onCheckedChange = { hasReminder = it })
                    Text("Daily reminder")
                }
                if (hasReminder) {
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
                val reminderHour = if (hasReminder) hourText.toIntOrNull()?.coerceIn(0, 23) ?: 8 else null
                val reminderMinute = if (hasReminder) minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0 else null
                onAdd(
                    AddHabitResult(
                        name = name,
                        emoji = emoji,
                        colorHex = colorHex,
                        frequency = HabitFrequency.DAILY,
                        activeDaysOfWeek = emptySet(),
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                )
            }) {
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

data class AddHabitResult(
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequency: HabitFrequency,
    val activeDaysOfWeek: Set<Int>,
    val reminderHour: Int?,
    val reminderMinute: Int?
)

@Composable
private fun EmojiChip(emoji: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .selectable(selected = selected, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ColorSwatch(hex: String, selected: Boolean, onClick: () -> Unit) {
    val color = com.harshiitx.habittickoff.ui.common.parseColorHex(hex)
    Column(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else {
                    Modifier
                }
            )
            .selectable(selected = selected, onClick = onClick)
    ) {}
}
