package com.harshiitx.habittickoff.ui.justification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val MIN_JUSTIFICATION_WORDS = 30

/**
 * Deliberately a full-screen [Surface] at the content root, not a
 * [androidx.compose.ui.window.Dialog] — a real Dialog opens its own Android
 * Window with its own back-press/tap-outside dismiss semantics to fight.
 * A same-window Surface has no "outside" to tap, and [BackHandler] swallows
 * the back gesture, so the only way through is typing a real justification.
 */
@Composable
fun MandatoryJustificationDialog(item: PendingJustificationItem, onSubmit: (String) -> Unit) {
    var text by rememberSaveable(item.epochDay, item.title) { mutableStateOf("") }
    val wordCount = remember(text) { text.trim().split(Regex("\\s+")).count { it.isNotBlank() } }
    val dateLabel = remember(item.epochDay) {
        LocalDate.ofEpochDay(item.epochDay).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    BackHandler(enabled = true) { /* swallow back press: no dismiss without justifying */ }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("You missed \"${item.title}\"", style = MaterialTheme.typography.headlineSmall)
            Text("on $dateLabel", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Why did you miss it, and what will you do differently next time? " +
                    "(at least $MIN_JUSTIFICATION_WORDS words)"
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("I missed this because...") },
                minLines = 6
            )
            Text("$wordCount / $MIN_JUSTIFICATION_WORDS words")
            Button(
                onClick = { onSubmit(text) },
                enabled = wordCount >= MIN_JUSTIFICATION_WORDS,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit and continue")
            }
        }
    }
}
