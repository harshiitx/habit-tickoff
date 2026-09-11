package com.harshiitx.habittickoff.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF1B1300),
    primaryContainer = Color(0xFF4A3200),
    onPrimaryContainer = Color(0xFFFFDDB0),
    secondary = Color(0xFF6FCF97),
    onSecondary = Color(0xFF07290E),
    background = Color(0xFF121214),
    onBackground = Color(0xFFECECEC),
    surface = Color(0xFF1A1A1D),
    onSurface = Color(0xFFECECEC),
    surfaceVariant = Color(0xFF242429),
    onSurfaceVariant = Color(0xFFCFCFCF)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFB8720A),
    secondary = Color(0xFF2E7D5B)
)

@Composable
fun HabitTickoffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}
