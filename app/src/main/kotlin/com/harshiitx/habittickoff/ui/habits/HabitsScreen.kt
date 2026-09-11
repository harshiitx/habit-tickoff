package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun HabitsScreen(viewModel: HabitsViewModel) {
    val habits by viewModel.habits.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val today = remember { LocalDate.now().toEpochDay() }

    var pendingBackdate by remember { mutableStateOf<Pair<String, Long>?>(null) }

    Scaffold { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(horizontal = 12.dp)) {
            items(habits.filterNot { it.isArchived }, key = { it.id }) { habit ->
                val statusByEpochDay = logs
                    .filter { it.habitId == habit.id }
                    .associate { it.epochDay to it.status }

                HabitCard(
                    habit = habit,
                    statusByEpochDay = statusByEpochDay,
                    today = today,
                    onMarkDoneToday = { viewModel.markDoneToday(habit.id) },
                    onDayClick = { epochDay -> pendingBackdate = habit.id to epochDay }
                )
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
            onDismiss = { pendingBackdate = null }
        )
    }
}
