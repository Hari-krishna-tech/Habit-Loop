package com.habitstreak.habitloop.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

@SuppressLint("ScheduleExactAlarm")
fun scheduleReminder(context: Context, dayOfWeek: Int, hour: Int, minute: Int) {
    val triggerTime = calculateNextTriggerTime(dayOfWeek, hour, minute)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("DAY_OF_WEEK", dayOfWeek)
        putExtra("HOUR", hour)
        putExtra("MINUTE", minute)
    }

    val requestCode = generateUniqueRequestCode(dayOfWeek, hour, minute)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        flags
    )

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        // Fallback to inexact alarm if permission not granted
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}

private fun calculateNextTriggerTime(dayOfWeek: Int, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
    }

    return calendar.timeInMillis
}

private fun generateUniqueRequestCode(dayOfWeek: Int, hour: Int, minute: Int): Int {
    return dayOfWeek * 10000 + hour * 100 + minute
}

fun mapDayStringToCalendarDay(day: String): Int {
    return when (day) {
        "Mon" -> Calendar.MONDAY
        "Tue" -> Calendar.TUESDAY
        "Wed" -> Calendar.WEDNESDAY
        "Thu" -> Calendar.THURSDAY
        "Fri" -> Calendar.FRIDAY
        "Sat" -> Calendar.SATURDAY
        "Sun" -> Calendar.SUNDAY
        else -> throw IllegalArgumentException("Invalid day string: $day")
    }
}