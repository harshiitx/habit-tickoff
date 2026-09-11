package com.harshiitx.habittickoff.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.data.model.CompletionStatus

private val DoneColor = Color(0xFF6FCF97)
private val MissedColor = Color(0xFFEB5757)
private val EmptyColor = Color(0xFF2A2A2E)

/**
 * A GitHub-contributions-style grid: [weeksToShow] columns of 7 days each,
 * ending at [today]. Stateless — callers own windowing/lookup; tapping a
 * past-or-today cell reports its epoch day via [onDayClick] for backdating.
 */
@Composable
fun HabitHeatmap(
    statusByEpochDay: Map<Long, CompletionStatus>,
    today: Long,
    modifier: Modifier = Modifier,
    weeksToShow: Int = 20,
    cellSize: Dp = 12.dp,
    cellGap: Dp = 3.dp,
    onDayClick: (Long) -> Unit = {}
) {
    val density = LocalDensity.current
    val step = cellSize + cellGap
    val stepPx = with(density) { step.toPx() }
    val cellSizePx = with(density) { cellSize.toPx() }
    val gridStart = today - (weeksToShow * 7L) + 1

    Canvas(
        modifier = modifier
            .size(width = step * weeksToShow, height = step * 7)
            .pointerInput(statusByEpochDay, today, weeksToShow) {
                detectTapGestures { offset ->
                    val week = (offset.x / stepPx).toInt()
                    val dayOfWeekColumn = (offset.y / stepPx).toInt()
                    val epochDay = gridStart + week * 7 + dayOfWeekColumn
                    if (epochDay in gridStart..today) onDayClick(epochDay)
                }
            }
    ) {
        for (week in 0 until weeksToShow) {
            for (dayOfWeekColumn in 0 until 7) {
                val epochDay = gridStart + week * 7 + dayOfWeekColumn
                if (epochDay > today) continue
                val color = when (statusByEpochDay[epochDay]) {
                    CompletionStatus.DONE -> DoneColor
                    CompletionStatus.MISSED -> MissedColor
                    else -> EmptyColor
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(week * stepPx, dayOfWeekColumn * stepPx),
                    size = Size(cellSizePx, cellSizePx),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }
    }
}
