package com.example.paidly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paidly.data.local.PaymentReminderDao
import com.example.paidly.data.local.PaymentReminderEntity
import com.example.paidly.data.model.PaymentStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val dao: PaymentReminderDao) : ViewModel() {

    // Flow for all reminders
    val reminders = dao.getAllReminders()
        .map { it.sortedBy { reminder -> reminder.dueDate } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow for received (history) reminders
    val historyReminders = dao.getReceivedReminders()
        .map { it.sortedByDescending { it.dueDate } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(reminder: PaymentReminderEntity) {
        viewModelScope.launch {
            dao.insertReminder(reminder.copy(id = 0))
        }
    }

    suspend fun addReminderAndReturn(reminder: PaymentReminderEntity): PaymentReminderEntity {
        val insertedId: Long = dao.insertReminder(reminder.copy(id = 0))
        return reminder.copy(id = insertedId.toInt())
    }

    fun updateReminder(reminder: PaymentReminderEntity) {
        viewModelScope.launch {
            dao.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: PaymentReminderEntity) {
        viewModelScope.launch {
            dao.deleteReminder(reminder)
        }
    }

    fun markAsReceived(reminder: PaymentReminderEntity, paidDate: String) {
        viewModelScope.launch {
            val updated = reminder.copy(
                isReceived = true,
                paidDate = paidDate,
                status = PaymentStatus.PAST.name
            )
            dao.updateReminder(updated)

            val dueDate = LocalDate.parse(reminder.dueDate)
            val nextDue = when (reminder.recurringType) {
                "Daily" -> dueDate.plusDays(1)
                "Weekly" -> dueDate.plusWeeks(1)
                "Monthly" -> dueDate.plusMonths(1)
                else -> return@launch
            }

            val newReminder = reminder.copy(
                id = 0,
                isReceived = false,
                paidDate = null,
                dueDate = nextDue.toString(),
                month = "${nextDue.month.name} ${nextDue.year}",
                status = PaymentStatus.FUTURE.name,
                note = "", // Clear note for next reminder
                direction = reminder.direction // ✅ PRESERVE TO_PAY or TO_RECEIVE
            )

            dao.insertReminder(newReminder)
        }
    }

}