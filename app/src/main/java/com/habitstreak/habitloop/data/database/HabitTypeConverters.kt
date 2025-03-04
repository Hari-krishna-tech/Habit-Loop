package com.habitstreak.habitloop.data.database

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalDateTime

class HabitTypeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String {
        return dateTime?.toString() ?: LocalDateTime.now().toString();
    }

    @SuppressLint("NewApi")
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return try {
            value?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now().minusDays(1)
        } catch (e: Exception) {
            LocalDateTime.now().minusDays(1)
        }
    }
}