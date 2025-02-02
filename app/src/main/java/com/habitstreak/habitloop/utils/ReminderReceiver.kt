package com.habitstreak.habitloop.utils// ReminderReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Create a notification channel if running on Android O and above.
        NotificationHelper.createNotificationChannel(context)

        // Build the notification
        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your own icon
            .setContentTitle("Reminder")
            .setContentText("It's time for your reminder!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}
