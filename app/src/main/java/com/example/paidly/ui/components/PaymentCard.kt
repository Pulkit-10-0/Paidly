package com.example.paidly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paidly.data.local.PaymentReminderEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun PaymentCard(
    reminder: PaymentReminderEntity,
    onClick: (PaymentReminderEntity) -> Unit,
    cardColor: Color? = null,
    extraLabel: String? = null
) {
    val dueDate = LocalDate.parse(reminder.dueDate)
    val today = LocalDate.now()
    val daysLeft = ChronoUnit.DAYS.between(today, dueDate)

    val defaultColor = when {
        !reminder.isReceived && daysLeft < 0 -> Color(0xFFB71C1C)
        !reminder.isReceived && daysLeft == 0L -> Color(0xFFE53935)
        !reminder.isReceived && daysLeft in 1..3 -> Color(0xFFFB8C00)
        !reminder.isReceived -> Color(0xFF43A047)
        else -> Color(0xFFBDBDBD)
    }

    val actualColor = cardColor ?: defaultColor

    val paymentDirection = when (reminder.direction) {
        "TO_PAY" -> "To Pay"
        "TO_RECEIVE" -> "To Receive"
        else -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(reminder) },
        colors = CardDefaults.cardColors(
            containerColor = actualColor,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            extraLabel?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Amount: ₹${reminder.amount}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(5.dp))

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            val formattedDueDate = dueDate.format(formatter)

            when {
                reminder.isReceived -> {
                    val formattedPaidDate = reminder.paidDate?.let {
                        LocalDate.parse(it).format(formatter)
                    } ?: "Unknown"
                    Text("Status: Received on $formattedPaidDate")
                }
                reminder.status == "PARTIALLY_PAID" -> {
                    val paid = reminder.partialAmountPaid ?: 0.0
                    val remaining = reminder.amount - paid
                    val newDue = reminder.partialDueDate?.let {
                        LocalDate.parse(it).format(formatter)
                    } ?: formattedDueDate
                    Text("Partially paid: ₹$paid")
                    Text("Remaining: ₹$remaining")
                    Text("Next due: $newDue")
                }
                dueDate.isBefore(today) -> {
                    Text("Status: Overdue", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Text("Due: $formattedDueDate")
                }
                else -> {
                    Text("Due: $formattedDueDate")
                    Text("Status: Pending")
                }
            }

            Spacer(Modifier.height(3.dp))
            Text("Repeat: ${reminder.recurringType}")
            Spacer(Modifier.height(3.dp))
            Text("Direction: $paymentDirection")

            if (reminder.note.isNotBlank()) {
                Spacer(Modifier.height(3.dp))
                Text("Note: ${reminder.note}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentCardPreview() {
    val dummyReminder = PaymentReminderEntity(
        id = 1,
        name = "Test Payment",
        amount = 2500.0,
        dueDate = LocalDate.now().plusDays(-1).toString(),
        isReceived = false,
        status = "PARTIALLY_PAID",
        partialAmountPaid = 500.0,
        partialDueDate = LocalDate.now().plusDays(7).toString(),
        personName = "John Doe",
        month = "JULY 2025",
        recurringType = "Monthly",
        direction = "TO_PAY",
        note = "This is a test note"
    )

    MaterialTheme {
        PaymentCard(reminder = dummyReminder, onClick = {})
    }
}