package com.harshiitx.habittickoff.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Parses "#RRGGBB" (falls back to a neutral gray on anything malformed). */
fun parseColorHex(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrDefault(Color(0xFF888888))

/** A dimmed, desaturated version of a habit color for "not done yet" cells. */
fun Color.dimmedForEmpty(background: Color = Color(0xFF1C1C1E)): Color = lerp(background, this, 0.18f)
