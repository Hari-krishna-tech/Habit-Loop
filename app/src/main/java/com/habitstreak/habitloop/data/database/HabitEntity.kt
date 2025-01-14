package com.habitstreak.habitloop.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime



@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val emoji: String,
    val frequency: List<String>,
    val curStreak: Int = 0,
    val highestStreak: Int = 0,
    val acitivity: List<String>,
    val lastStreakModified: LocalDateTime,
    val isReminderSet: Boolean = false,
    val reminderTime: LocalDateTime? = null
)
