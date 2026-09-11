package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackdateBottomSheet(
    epochDay: Long,
    onMarkDone: () -> Unit,
    onMarkMissed: () -> Unit,
    onDismiss: () -> Unit
) {
    val date = LocalDate.ofEpochDay(epochDay)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(date.format(formatter))
            Button(modifier = Modifier.fillMaxWidth(), onClick = onMarkDone) {
                Text("Mark Done")
            }
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onMarkMissed) {
                Text("Mark Missed")
            }
        }
    }
}
