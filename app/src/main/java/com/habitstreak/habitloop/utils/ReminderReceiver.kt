package com.habitstreak.habitloop.utils// ReminderReceiver.kt
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.habitstreak.habitloop.R

// Modified ReminderReceiver.kt
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Create the notification channel
        NotificationHelper.createNotificationChannel(context)

        val habitTitle = intent?.getStringExtra("HABIT_TITLE") ?: "Habit Reminder"

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(habitTitle)
            .setContentText("Time to complete your habit!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                generateUniqueRequestCode(
                    intent?.getIntExtra("DAY_OF_WEEK", 1) ?: 1,
                    intent?.getIntExtra("HOUR", 0) ?: 0,
                    intent?.getIntExtra("MINUTE", 0) ?: 0
                ),
                notification
            )
        }

        // Reschedule next reminder
        val dayOfWeek = intent?.getIntExtra("DAY_OF_WEEK", -1) ?: -1
        val hour = intent?.getIntExtra("HOUR", -1) ?: -1
        val minute = intent?.getIntExtra("MINUTE", -1) ?: -1

        if (dayOfWeek != -1 && hour != -1 && minute != -1) {
            scheduleReminder(context, dayOfWeek, hour, minute)
        }
    }
}