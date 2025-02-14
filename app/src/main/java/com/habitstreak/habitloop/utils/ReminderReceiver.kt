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

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder")
            .setContentText("It's time for your habit!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }

        // Reschedule next reminder
        val dayOfWeek = intent?.getIntExtra("DAY_OF_WEEK", -1) ?: -1
        val hour = intent?.getIntExtra("HOUR", -1) ?: -1
        val minute = intent?.getIntExtra("MINUTE", -1) ?: -1

        if (dayOfWeek != -1 && hour != -1 && minute != -1) {
            scheduleReminder(context, dayOfWeek, hour, minute)
        }
    }}
