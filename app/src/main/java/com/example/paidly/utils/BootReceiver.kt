package com.example.paidly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val (hour, minute) = NotificationPreferenceManager.getNotificationTime(context)
            scheduleDailyReminder(context, hour, minute)
        }
    }
}
