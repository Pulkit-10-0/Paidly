package com.example.paidly.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.paidly.data.local.PaidlyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val now = LocalTime.now()
        val today = LocalDate.now().toString()

        Log.d("PaidlyNotif", "🔄 Worker running at $now")

        val (savedHour, savedMinute) = NotificationPreferenceManager.getNotificationTime(context)
        val lastShownDate = NotificationPreferenceManager.getLastNotificationDate(context)

        Log.d("PaidlyNotif", "🔍 Last shown date: $lastShownDate | Today: $today")

        // Avoid showing multiple times on same day
        if (lastShownDate == today) {
            Log.d("PaidlyNotif", "❌ Notification already shown today")
            return Result.success()
        }

        val targetTime = LocalTime.of(savedHour, savedMinute)
        val minutesDiff = Duration.between(targetTime, now).toMinutes()

        // Allow margin of ±1 minute
        if (minutesDiff in -2..2)  {
            Log.d("PaidlyNotif", "✅ Time matched (±1 min), checking due reminders")

            val dao = PaidlyDatabase.getInstance(context).paymentReminderDao()

            val reminders = withContext(Dispatchers.IO) {
                dao.getDueReminders(today)
            }

            if (reminders.isNotEmpty()) {
                Log.d("PaidlyNotif", "📬 Sending ${reminders.size} notifications")

                reminders.forEach {
                    showDueNotification(context, it.name, it.id)
                }

                NotificationPreferenceManager.saveLastNotificationDate(context, today)
            } else {
                Log.d("PaidlyNotif", "📭 No due reminders for today")
            }
        } else {
            Log.d("PaidlyNotif", "⌛ Time not matched. Current: $now | Target: $targetTime (diff=$minutesDiff min)")
        }

        return Result.success()
    }
}
