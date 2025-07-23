package com.example.paidly.utils

import android.content.Context

private const val PREFS_NAME = "notification_prefs"
private const val HOUR_KEY = "hour"
private const val MINUTE_KEY = "minute"
private const val LAST_SHOWN_DATE = "last_shown_date"

object NotificationPreferenceManager {

    fun saveNotificationTime(context: Context, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(HOUR_KEY, hour).putInt(MINUTE_KEY, minute).apply()
    }

    fun getNotificationTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(HOUR_KEY, 9)  // default 9 AM
        val minute = prefs.getInt(MINUTE_KEY, 0)
        return Pair(hour, minute)
    }

    fun saveLastNotificationDate(context: Context, date: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LAST_SHOWN_DATE, date).apply()
    }

    fun getLastNotificationDate(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LAST_SHOWN_DATE, null)
    }
}
