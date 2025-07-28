package com.example.paidly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PaymentReminderEntity::class],
    version = 6,
    exportSchema = false
)
abstract class PaidlyDatabase : RoomDatabase() {
    abstract fun paymentReminderDao(): PaymentReminderDao

    companion object {
        @Volatile
        private var INSTANCE: PaidlyDatabase? = null

        fun getInstance(context: Context): PaidlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaidlyDatabase::class.java,
                    "paidly_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
