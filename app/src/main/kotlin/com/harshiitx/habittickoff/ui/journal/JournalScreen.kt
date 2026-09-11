package com.harshiitx.habittickoff.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshiitx.habittickoff.ui.common.ruledPaperBackground
import com.harshiitx.habittickoff.ui.theme.HandwrittenFontFamily
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun JournalScreen(viewModel: JournalViewModel) {
    val selectedEpochDay by viewModel.selectedEpochDay.collectAsState()
    val entries by viewModel.entries.collectAsState()
    val dateLabel = remember(selectedEpochDay) {
        LocalDate.ofEpochDay(selectedEpochDay).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    }
    val todayEpochDay = remember { LocalDate.now().toEpochDay() }

    var draftText by remember(selectedEpochDay) {
        mutableStateOf(entries.firstOrNull { it.epochDay == selectedEpochDay }?.text.orEmpty())
    }
    LaunchedEffect(entries, selectedEpochDay) {
        val loaded = entries.firstOrNull { it.epochDay == selectedEpochDay }?.text
        if (draftText.isEmpty() && !loaded.isNullOrEmpty()) {
            draftText = loaded
        }
    }

    Column(modifier = Modifier.fillMaxSize().ruledPaperBackground()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.selectDay(selectedEpochDay - 1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous day")
            }
            Text(
                dateLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = HandwrittenFontFamily),
                color = Color(0xFF2B2B2B)
            )
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
        TextField(
            value = draftText,
            onValueChange = {
                draftText = it
                viewModel.updateText(it)
            },
            modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
            placeholder = { Text("Dear diary...") },
            textStyle = TextStyle(fontFamily = HandwrittenFontFamily, fontSize = 22.sp, color = Color(0xFF2B2B2B)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}
