package com.example.paidly.utils

import android.content.Context
import android.util.Log
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
    Log.d("PaidlyNotif", "⏰ Scheduling daily notification at $hour:$minute")

    WorkManager.getInstance(context).cancelUniqueWork("DailyNotification")

    val now = LocalDateTime.now()
    val targetTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    val delay = if (targetTime.isAfter(now)) {
        Duration.between(now, targetTime).toMinutes()
    } else {
        Duration.between(now, targetTime.plusDays(1)).toMinutes()
    }

    Log.d("PaidlyNotif", "📅 Initial delay is $delay minutes")

    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        24, TimeUnit.HOURS
    )
        .setInitialDelay(delay, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyNotification",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )

    Log.d("PaidlyNotif", "✅ WorkManager job enqueued")
}
