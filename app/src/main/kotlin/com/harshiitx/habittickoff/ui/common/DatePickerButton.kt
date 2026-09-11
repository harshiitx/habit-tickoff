package com.harshiitx.habittickoff.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private const val MILLIS_PER_DAY = 86_400_000L

/** A calendar icon that opens a Material date picker; the picker works in UTC-midnight
 * millis, which lines up directly with [java.time.LocalDate.toEpochDay]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(selectedEpochDay: Long, onDateSelected: (Long) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    IconButton(onClick = { showPicker = true }) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick a date")
    }

    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = selectedEpochDay * MILLIS_PER_DAY)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis -> onDateSelected(millis / MILLIS_PER_DAY) }
                    showPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }
}
