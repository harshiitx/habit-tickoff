package com.harshiitx.habittickoff.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppRoute(val route: String, val label: String, val icon: ImageVector) {
    HABITS("habits", "Habits", Icons.Filled.CheckCircle),
    PLANNER("planner", "Planner", Icons.Filled.EventNote),
    JOURNAL("journal", "Journal", Icons.Filled.Book),
    QUOTES("quotes", "Quotes", Icons.Filled.FormatQuote),
    SETTINGS("settings", "Settings", Icons.Filled.Settings)
}
