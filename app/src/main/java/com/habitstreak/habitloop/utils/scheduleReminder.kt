package com.habitstreak.habitloop.utils// ReminderScheduler.kt
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

fun scheduleReminder(context: Context, triggerAtMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Create an intent to trigger the ReminderReceiver
    val intent = Intent(context, ReminderReceiver::class.java)

    // Create a PendingIntent with appropriate flags
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    } else {
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Schedule the alarm
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP, // Use RTC_WAKEUP to wake the device if asleep
        triggerAtMillis,
        pendingIntent
    )
}
