package com.harshiitx.habittickoff.ui.common

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val PaperColor = Color(0xFFFFFDF6)
val PaperLineColor = Color(0x1A2B2B2B)

/** A plain paper tint with faint ruled lines — just enough "real paper" feel without clutter. */
fun Modifier.ruledPaperBackground(
    paperColor: Color = PaperColor,
    lineColor: Color = PaperLineColor,
    lineSpacing: Dp = 32.dp
): Modifier = this
    .background(paperColor)
    .drawWithCache {
        val spacingPx = lineSpacing.toPx()
        onDrawBehind {
            var y = spacingPx
            while (y < size.height) {
                drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += spacingPx
            }
        }
    }
