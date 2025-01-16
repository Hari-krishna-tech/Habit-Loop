package com.habitstreak.habitloop.data.repository

import com.habitstreak.habitloop.data.database.HabitDao
import com.habitstreak.habitloop.data.database.HabitEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    // getting all the habits
    // need to work in this tomorrow confirm
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    suspend fun addOrUpdateHabit(habit: HabitEntity) {
        habitDao.upsertHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    suspend fun getHabitById(habitId: Int): HabitEntity? {
        return habitDao.getHabitById(habitId)
    }
}