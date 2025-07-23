package com.example.paidly.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentReminderDao {
    @Query("SELECT * FROM payment_reminders")
    fun getAllReminders(): Flow<List<PaymentReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: PaymentReminderEntity): Long


    @Update
    suspend fun updateReminder(reminder: PaymentReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: PaymentReminderEntity)
    @Query("SELECT * FROM payment_reminders WHERE isReceived = 1")
    fun getReceivedReminders(): Flow<List<PaymentReminderEntity>>


    @Query("SELECT * FROM payment_reminders WHERE isReceived = 0 AND dueDate = :today")
    suspend fun getDueReminders(today: String): List<PaymentReminderEntity>

}
