package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.ui.common.HabitHeatmap

fun currentStreak(doneDays: Set<Long>, today: Long): Int {
    var streak = 0
    var day = today
    while (doneDays.contains(day)) {
        streak++
        day--
    }
    return streak
}

@Composable
fun HabitCard(
    habit: Habit,
    statusByEpochDay: Map<Long, CompletionStatus>,
    today: Long,
    onMarkDoneToday: () -> Unit,
    onDayClick: (Long) -> Unit
) {
    val doneDays = statusByEpochDay.filterValues { it == CompletionStatus.DONE }.keys
    val streak = currentStreak(doneDays, today)
    val doneToday = statusByEpochDay[today] == CompletionStatus.DONE

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "${habit.emoji}  ${habit.name}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Streak: $streak", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onMarkDoneToday) {
                    Icon(
                        imageVector = if (doneToday) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Mark done today"
                    )
                }
            }
            HabitHeatmap(
                statusByEpochDay = statusByEpochDay,
                today = today,
                modifier = Modifier.padding(top = 12.dp),
                onDayClick = onDayClick
            )
        }
    }
}
