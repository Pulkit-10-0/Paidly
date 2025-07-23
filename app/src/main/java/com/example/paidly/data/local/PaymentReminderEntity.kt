    package com.example.paidly.data.local

    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey
    import com.example.paidly.data.model.PaymentStatus

    @Entity(tableName = "payment_reminders")
    data class PaymentReminderEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val name: String,
        val amount: Double,
        val dueDate: String,
        val isReceived: Boolean,
        val status: String,
        val personName: String,
        val month: String,
        val recurringType: String = "None",
        val recurringGroupId: String? = null,
        val note: String = "",
        val paidDate: String? = null,
        val direction: String = "TO_RECEIVE"
    )
