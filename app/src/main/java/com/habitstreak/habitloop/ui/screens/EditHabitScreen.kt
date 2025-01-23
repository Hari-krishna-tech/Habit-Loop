package com.habitstreak.habitloop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habitId: Int?,
    navController: NavController,
    viewModel: HabitViewModel
) {
    var habit by remember { mutableStateOf<HabitEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("😊") }
    var frequency by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(habitId) {
        habit = habitId?.let { viewModel.getHabitById(it) }
        if (habit == null) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        title = habit?.title ?: ""
        emoji = habit?.emoji ?: "😊"
        frequency = habit?.frequency?.toSet() ?: setOf()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Habit") },
                actions = {
                    TextButton(
                        onClick = {
                            habit?.let { currentHabit ->
                                viewModel.addOrUpdateHabit(
                                    currentHabit.copy(
                                        title = title,
                                        emoji = emoji,
                                        frequency = frequency.toList()
                                    )
                                )
                            }
                            navController.popBackStack()
                        },
                        enabled = title.isNotBlank() && frequency.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
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
        }
    }
}