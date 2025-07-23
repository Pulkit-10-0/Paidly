package com.example.paidly.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.paidly.data.local.PaymentReminderEntity
import com.example.paidly.ui.components.PaymentCard
import com.example.paidly.ui.components.ReminderDetailsBottomSheet
import com.example.paidly.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    viewModel: HomeViewModel,
    drawerState: DrawerState
) {
    val reminders by viewModel.reminders.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedReminder by remember { mutableStateOf<PaymentReminderEntity?>(null) }

    val completedReminders = remember(reminders) {
        reminders.filter { it.isReceived }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredReminders = remember(searchQuery, completedReminders) {
        if (searchQuery.isBlank()) completedReminders
        else completedReminders.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.personName.contains(searchQuery, ignoreCase = true) ||
                    it.amount.toString().contains(searchQuery)
        }
    }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name, person, or amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (filteredReminders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching reminders found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReminders) { reminder ->
                        val cardColor = if (reminder.amount < 0)
                            Color(0xFFA5D6A7) // Green for received
                        else
                            Color(0xFFF44336) // Red for paid

                        PaymentCard(
                            reminder = reminder,
                            onClick = { selectedReminder = it },
                            cardColor = cardColor
                        )
                    }
                }
            }
        }
    }

    // Bottom sheet for details
    selectedReminder?.let { reminder ->
        ReminderDetailsBottomSheet(
            reminder = reminder,
            onDismiss = { selectedReminder = null },
            onDelete = { rem ->
                coroutineScope.launch {
                    viewModel.deleteReminder(rem)
                    val result = snackbarHostState.showSnackbar(
                        message = "Reminder deleted",
                        actionLabel = "UNDO",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.addReminder(rem)
                    }
                }
            },
            onMarkAsReceived = { rem, paidDate -> viewModel.markAsReceived(rem, paidDate) },
            onNoteChange = { viewModel.updateReminder(it) }
        )
    }
}
