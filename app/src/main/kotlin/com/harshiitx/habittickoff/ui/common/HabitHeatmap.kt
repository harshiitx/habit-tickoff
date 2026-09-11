package com.harshiitx.habittickoff.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private val MissedColor = Color(0xFFEB5757)

/**
 * A GitHub-contributions-style grid: [weeksToShow] columns of 7 days each,
 * ending at [today], horizontally scrollable (auto-scrolled to today on
 * first show) so cells can stay large and easy to tap. Stateless — callers
 * own windowing/lookup; tapping a past-or-today cell reports its epoch day
 * via [onDayClick] for backdating.
 */
@Composable
fun HabitHeatmap(
    statusByEpochDay: Map<Long, CompletionStatus>,
    today: Long,
    habitColor: Color,
    modifier: Modifier = Modifier,
    weeksToShow: Int = 18,
    cellSize: Dp = 26.dp,
    cellGap: Dp = 6.dp,
    onDayClick: (Long) -> Unit = {}
) {
    val density = LocalDensity.current
    val step = cellSize + cellGap
    val stepPx = with(density) { step.toPx() }
    val cellSizePx = with(density) { cellSize.toPx() }
    val gridStart = today - (weeksToShow * 7L) + 1
    val doneColor = habitColor
    val emptyColor = habitColor.dimmedForEmpty()

    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.maxValue }.filter { it > 0 }.first().let { scrollState.scrollTo(it) }
    }

    Canvas(
        modifier = modifier
            .horizontalScroll(scrollState)
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
                    CompletionStatus.DONE -> doneColor
                    CompletionStatus.MISSED -> MissedColor
                    else -> emptyColor
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(week * stepPx, dayOfWeekColumn * stepPx),
                    size = Size(cellSizePx, cellSizePx),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }
    }
}
