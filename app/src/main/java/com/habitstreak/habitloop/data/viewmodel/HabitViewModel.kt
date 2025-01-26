package com.habitstreak.habitloop.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val allHabits: StateFlow<List<HabitEntity>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addOrUpdateHabit(habit: HabitEntity) = viewModelScope.launch {
        repository.addOrUpdateHabit(habit)
    }

    fun deleteHabit(habit: HabitEntity) = viewModelScope.launch {
        repository.deleteHabit(habit)
    }

    suspend fun getHabitById(habitId: Int): HabitEntity? {
        return repository.getHabitById(habitId)
    }


    suspend fun importHabits(habits: List<HabitEntity>) {
        // Implement logic to clear existing habits and add new ones
        // Consider showing a confirmation dialog first
        val existingHaibts = repository.allHabits.first()
        existingHaibts.forEach {
            deleteHabit(it);
        }

        habits.forEach {
            addOrUpdateHabit(it);
        }
    }
}