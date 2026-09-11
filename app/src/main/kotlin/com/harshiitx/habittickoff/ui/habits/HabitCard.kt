package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.ui.common.HabitHeatmap
import com.harshiitx.habittickoff.ui.common.dimmedForEmpty
import com.harshiitx.habittickoff.ui.common.parseColorHex

/**
 * Current streak counts consecutive DONE days ending today; if today just
 * hasn't been marked yet (as opposed to explicitly MISSED), it counts from
 * yesterday instead so the streak doesn't drop to zero every morning before
 * you've had a chance to do it.
 */
fun currentStreak(statusByEpochDay: Map<Long, CompletionStatus>, today: Long): Int {
    if (statusByEpochDay[today] == CompletionStatus.MISSED) return 0
    var day = if (statusByEpochDay[today] == CompletionStatus.DONE) today else today - 1
    var streak = 0
    while (statusByEpochDay[day] == CompletionStatus.DONE) {
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
    onToggleDoneToday: () -> Unit,
    onDayClick: (Long) -> Unit
) {
    val habitColor = parseColorHex(habit.colorHex)
    val streak = currentStreak(statusByEpochDay, today)
    val doneToday = statusByEpochDay[today] == CompletionStatus.DONE

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(habitColor.dimmedForEmpty(background = Color.Black), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(habit.emoji, style = MaterialTheme.typography.titleLarge)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(habit.name, style = MaterialTheme.typography.titleMedium)
                        Text("Streak: $streak", style = MaterialTheme.typography.bodySmall)
                    }
                }
                IconButton(
                    onClick = onToggleDoneToday,
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (doneToday) habitColor else habitColor.dimmedForEmpty(Color.Black), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Toggle done today",
                        tint = if (doneToday) Color.Black else habitColor
                    )
                }
            }
            HabitHeatmap(
                statusByEpochDay = statusByEpochDay,
                today = today,
                habitColor = habitColor,
                modifier = Modifier.padding(top = 16.dp),
                onDayClick = onDayClick
            )
        }
    }
}
