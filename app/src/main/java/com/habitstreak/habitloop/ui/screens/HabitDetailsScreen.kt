package com.habitstreak.habitloop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Int?,
    navController: NavController,
    viewModel: HabitViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    var habit:HabitEntity? by remember { mutableStateOf(null) }

    LaunchedEffect(habitId) {
        habit = habitId?.let { viewModel.getHabitById(it) } as HabitEntity?
        if (habit == null) {
            navController.popBackStack()
            return@LaunchedEffect
        }
    }

    habit?.let { currentHabit ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentHabit.title) },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate("${Destinations.EDIT_HABIT}/${currentHabit.id}")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    viewModel.deleteHabit(currentHabit)
                                    navController.popBackStack()
                                }
                            )
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
                // Emoji and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentHabit.emoji,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = currentHabit.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // Streaks
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentHabit.curStreak.toString(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text("Current Streak")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentHabit.highestStreak.toString(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text("Highest Streak")
                        }
                    }
                }

                // Frequency
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Frequency",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            currentHabit.frequency.forEach { day ->
                                Text(day)
                            }
                        }
                    }
                }

                // Reminder
                if (currentHabit.isReminderSet && currentHabit.reminderTime != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Reminder",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Set for ${currentHabit.reminderTime}")
                        }
                    }
                }

                // Activity Chart (GitHub-style)
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Activity",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // TODO: Implement GitHub-style activity chart
                    }
                }
            }
        }
    }
}