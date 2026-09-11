package com.harshiitx.habittickoff.ui.habits

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.CompletionStatus
import com.harshiitx.habittickoff.data.model.Habit
import com.harshiitx.habittickoff.data.model.HabitLog
import com.harshiitx.habittickoff.domain.isHabitDueOn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekStrip(habits: List<Habit>, logs: List<HabitLog>, today: Long, modifier: Modifier = Modifier) {
    val mondayEpochDay = run {
        val date = LocalDate.ofEpochDay(today)
        date.minusDays((date.dayOfWeek.value - 1).toLong()).toEpochDay()
    }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        for (offset in 0 until 7) {
            val epochDay = mondayEpochDay + offset
            val date = LocalDate.ofEpochDay(epochDay)
            val dueHabits = habits.filter { !it.isArchived && isHabitDueOn(it, epochDay) }
            val doneCount = dueHabits.count { habit ->
                logs.any { it.habitId == habit.id && it.epochDay == epochDay && it.status == CompletionStatus.DONE }
            }
            val fraction = if (dueHabits.isEmpty()) 0f else doneCount.toFloat() / dueHabits.size
            DayCircle(
                letter = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                number = date.dayOfMonth,
                fraction = fraction,
                isToday = epochDay == today
            )
        }
    }
}

@Composable
private fun DayCircle(letter: String, number: Int, fraction: Float, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(letter, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(38.dp)
                .then(
                    if (isToday) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(number.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
}
