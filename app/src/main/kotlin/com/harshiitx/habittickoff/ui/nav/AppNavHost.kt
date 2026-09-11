package com.harshiitx.habittickoff.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harshiitx.habittickoff.HabitTickoffApp
import com.harshiitx.habittickoff.ui.habits.HabitsScreen
import com.harshiitx.habittickoff.ui.habits.HabitsViewModel
import com.harshiitx.habittickoff.ui.journal.JournalScreen
import com.harshiitx.habittickoff.ui.planner.PlannerScreen
import com.harshiitx.habittickoff.ui.quotes.QuotesScreen
import com.harshiitx.habittickoff.ui.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val app = LocalContext.current.applicationContext as HabitTickoffApp
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            NavigationBar {
                AppRoute.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.HABITS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.HABITS.route) {
                val viewModel: HabitsViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { HabitsViewModel(app.habitRepository) }
                    }
                )
                HabitsScreen(viewModel)
            }
            composable(AppRoute.PLANNER.route) { PlannerScreen() }
            composable(AppRoute.JOURNAL.route) { JournalScreen() }
            composable(AppRoute.QUOTES.route) { QuotesScreen() }
            composable(AppRoute.SETTINGS.route) { SettingsScreen() }
        }
    }
}
