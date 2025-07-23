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
        daysLeft == 0L -> Color(0xFFE53935)
        daysLeft in 1..3-> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
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

            val formattedDueDate = dueDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            Text("Due: $formattedDueDate")
            Text("Repeat: ${reminder.recurringType}")
            if (reminder.isReceived) {
                val formattedPaidDate = reminder.paidDate?.let {
                    LocalDate.parse(it).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                } ?: "Unknown"
                Text("Status: Received on $formattedPaidDate")
            } else {
                Text("Status: Pending")
            }
            Text("Direction: $paymentDirection")
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
        dueDate = LocalDate.now().plusDays(5).toString(),
        isReceived = false,
        status = "FUTURE",
        personName = "John Doe",
        month = "JULY 2025",
        recurringType = "Monthly",
        direction = "TO_PAY"
    )

    MaterialTheme {
        PaymentCard(reminder = dummyReminder, onClick = {})
    }
}
