package com.harshiitx.habittickoff.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** Parses "#RRGGBB" (falls back to a neutral gray on anything malformed). */
fun parseColorHex(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrDefault(Color(0xFF888888))

/** A muted-but-still-colorful version of a habit color for "not done yet" cells/backgrounds. */
fun Color.dimmedForEmpty(background: Color = Color(0xFF242429)): Color = lerp(background, this, 0.38f)
