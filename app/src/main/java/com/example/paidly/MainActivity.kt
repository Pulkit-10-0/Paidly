package com.example.paidly

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.paidly.data.local.PaidlyDatabase
import com.example.paidly.navigation.AppNavigation
import com.example.paidly.ui.theme.PaidlyTheme
import com.example.paidly.ui.viewmodel.HomeViewModel
import com.example.paidly.ui.viewmodel.HomeViewModelFactory
import com.example.paidly.utils.createNotificationChannel

class MainActivity : ComponentActivity() {

    // 🔔 Modern permission launcher for notifications
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("✅ Notification permission granted")
        } else {
            println("❌ Notification permission denied")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Ask for POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // ✅ Create notification channel early
        createNotificationChannel(this)

        // ⚙️ Initialize Room + ViewModel
        val database = PaidlyDatabase.getInstance(applicationContext)
        val dao = database.paymentReminderDao()
        val factory = HomeViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // 🎨 Compose UI
        setContent {
            PaidlyTheme {
                val navController = rememberNavController()
                AppNavigation(viewModel = viewModel, navController = navController)
            }
        }
    }
}