package com.harshiitx.habittickoff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harshiitx.habittickoff.ui.justification.JustificationViewModel
import com.harshiitx.habittickoff.ui.justification.MandatoryJustificationDialog
import com.harshiitx.habittickoff.ui.nav.AppNavHost
import com.harshiitx.habittickoff.ui.theme.HabitTickoffTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as HabitTickoffApp
            HabitTickoffTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val justificationViewModel: JustificationViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                JustificationViewModel(
                                    app.habitRepository,
                                    app.taskRepository,
                                    app.settingsRepository
                                )
                            }
                        }
                    )
                    val pendingQueue by justificationViewModel.pendingQueue.collectAsState()
                    val nextPending = pendingQueue.firstOrNull()

                    if (nextPending != null) {
                        MandatoryJustificationDialog(
                            item = nextPending,
                            onSubmit = { text -> justificationViewModel.submitJustification(nextPending, text) }
                        )
                    } else {
                        AppNavHost()
                    }
                }
            }
        }
    }
}
