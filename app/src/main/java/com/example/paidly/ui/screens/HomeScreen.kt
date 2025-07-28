package com.example.paidly.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.paidly.data.local.PaymentReminderEntity
import com.example.paidly.data.model.PaymentStatus
import com.example.paidly.ui.components.PaymentCard
import com.example.paidly.ui.components.ReminderDetailsBottomSheet
import com.example.paidly.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    filterType: String = "ALL",
    drawerState: DrawerState
) {
    val reminders by viewModel.reminders.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf<PaymentReminderEntity?>(null) }
    val gridState = rememberLazyGridState()
    val context = LocalContext.current

    val filteredReminders = when (filterType) {
        "TO_PAY" -> reminders.filter { !it.isReceived && it.amount > 0 }
        "TO_RECEIVE" -> reminders.filter { !it.isReceived && it.amount < 0 }
        else -> reminders.filter { !it.isReceived }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paidly") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredReminders.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Text("No reminders yet.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                items(filteredReminders) { reminder ->
                    PaymentCard(
                        reminder = reminder.copy(amount = kotlin.math.abs(reminder.amount)), // Show absolute amount
                        onClick = { selectedReminder = it }
                    )
                }
            }
        }
    }

    if (showSheet) {
        var name by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(LocalDate.now()) }
        var recurrence by remember { mutableStateOf("None") }
        var direction by remember { mutableStateOf("TO_RECEIVE") }

        val recurrenceOptions = listOf("None", "Daily", "Weekly", "Monthly")
        val directions = mapOf("TO_PAY" to "To Pay", "TO_RECEIVE" to "To Receive")

        val isValid = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) != 0.0

        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Reminder", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    onValueChange = {},
                    label = { Text("Due Date") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val today = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    date = LocalDate.of(y, m + 1, d)
                                },
                                today.get(Calendar.YEAR),
                                today.get(Calendar.MONTH),
                                today.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                )

                Spacer(Modifier.height(12.dp))
                var recurrenceExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = !recurrenceExpanded }
                ) {
                    OutlinedTextField(
                        value = recurrence,
                        onValueChange = {},
                        label = { Text("Recurring") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(recurrenceExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        recurrenceOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    recurrence = it
                                    recurrenceExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = directions[direction] ?: "To Pay",
                        onValueChange = {},
                        label = { Text("Payment Type") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        directions.forEach { (internal, display) ->
                            DropdownMenuItem(
                                text = { Text(display) },
                                onClick = {
                                    direction = internal
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        val signedAmount = if (direction == "TO_RECEIVE") -amt else amt

                        val reminder = PaymentReminderEntity(
                            name = name,
                            amount = signedAmount,
                            dueDate = date.toString(),
                            isReceived = false,
                            status = PaymentStatus.FUTURE.name,
                            personName = name.trim(),
                            month = "${date.month.name} ${date.year}",
                            recurringType = recurrence,
                            note = "",
                            direction = direction
                        )

                        showSheet = false
                        coroutineScope.launch {
                            val inserted = viewModel.addReminderAndReturn(reminder)
                            gridState.scrollToItem(0)
                            val result = snackbarHostState.showSnackbar(
                                message = "Reminder added",
                                actionLabel = "UNDO",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.deleteReminder(inserted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValid
                ) {
                    Text("Add")
                }
            }
        }
    }

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
            onMarkAsReceived = { rem, paidDate ->
                viewModel.markAsReceived(rem, paidDate)
            },
            onNoteChange = { viewModel.updateReminder(it) }
        )
    }
}
