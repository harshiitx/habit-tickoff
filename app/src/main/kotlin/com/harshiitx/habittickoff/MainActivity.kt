package com.harshiitx.habittickoff

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harshiitx.habittickoff.ui.justification.JustificationViewModel
import com.harshiitx.habittickoff.ui.justification.MandatoryJustificationDialog
import com.harshiitx.habittickoff.ui.nav.AppNavHost
import com.harshiitx.habittickoff.ui.theme.HabitTickoffTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
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

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
