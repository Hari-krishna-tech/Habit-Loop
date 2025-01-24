package com.habitstreak.habitloop.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import java.time.LocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    var isCustomHabit by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("😊") }
    var frequency by remember { mutableStateOf(setOf<String>()) }
    var isReminderSet by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val predefinedHabits = listOf(
        PredefinedHabit("Exercise", "💪", setOf("Mon", "Wed", "Fri")),
        PredefinedHabit("Read", "📚", setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")),
        PredefinedHabit("Meditate", "🧘", setOf("Mon", "Tue", "Wed", "Thu", "Fri"))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isCustomHabit) "Create Custom Habit" else "Choose Habit") }
            )
        }
    ) { padding ->
        if (!isCustomHabit) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Button(
                        onClick = { isCustomHabit = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Create Custom Habit")
                    }
                }

                items(predefinedHabits) { predefinedHabit ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                title = predefinedHabit.title
                                emoji = predefinedHabit.emoji
                                frequency = predefinedHabit.frequency
                                isCustomHabit = true
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = predefinedHabit.emoji,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = predefinedHabit.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Frequency", style = MaterialTheme.typography.titleMedium)
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                Row(
                    modifier = Modifier
                        .selectableGroup()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEach { day ->
                        val selected = frequency.contains(day)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                frequency = if (selected) {
                                    frequency - day
                                } else {
                                    frequency + day
                                }
                            },
                            label = { Text(day) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isReminderSet,
                        onCheckedChange = { isReminderSet = it }
                    )
                    Text("Set Reminder")
                }

                if (isReminderSet) {
                    // Add Time Picker here
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && frequency.isNotEmpty()) {
                            viewModel.addOrUpdateHabit(
                                HabitEntity(
                                    title = title,
                                    emoji = emoji,
                                    frequency = frequency.toList(),
                                    lastStreakModified = LocalDateTime.now(),
                                    isReminderSet = isReminderSet,
                                    reminderTime = reminderTime,
                                    activity = emptyList()
                                )
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Habit")
                }
            }
        }
    }
}

data class PredefinedHabit(
    val title: String,
    val emoji: String,
    val frequency: Set<String>
)