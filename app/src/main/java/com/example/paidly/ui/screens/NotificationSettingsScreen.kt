package com.example.paidly.ui.screens

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.paidly.utils.NotificationPreferenceManager
import com.example.paidly.utils.scheduleDailyReminder
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    drawerState: DrawerState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var timeText by remember { mutableStateOf("Not Set") }

    LaunchedEffect(Unit) {
        val (h, m) = NotificationPreferenceManager.getNotificationTime(context)
        timeText = String.format("%02d:%02d", h, m)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text("Choose your preferred daily reminder time.", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Currently set to:", style = MaterialTheme.typography.labelMedium)
            Text(
                text = timeText,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val now = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            NotificationPreferenceManager.saveNotificationTime(context, hour, minute)
                            timeText = String.format("%02d:%02d", hour, minute)
                            scheduleDailyReminder(context, hour, minute)
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            ) {
                Text("Set Notification Time")
            }
        }
    }
}
