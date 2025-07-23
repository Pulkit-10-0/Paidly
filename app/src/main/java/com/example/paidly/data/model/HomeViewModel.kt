package com.example.paidly.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paidly.data.local.PaymentReminderDao
import com.example.paidly.data.local.PaymentReminderEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: PaymentReminderDao) : ViewModel() {

    val reminders = dao.getAllReminders()
        .map { list -> list.sortedBy { it.dueDate } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(reminder: PaymentReminderEntity) {
        viewModelScope.launch {
            dao.insertReminder(reminder)
        }
    }

    fun deleteReminder(reminder: PaymentReminderEntity) {
        viewModelScope.launch {
            dao.deleteReminder(reminder)
        }
    }
}
