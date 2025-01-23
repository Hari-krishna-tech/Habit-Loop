package com.habitstreak.habitloop.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.Destinations
import java.time.LocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    val habits by viewModel.allHabits.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Destinations.CREATE_HABIT) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No habits yet. Create one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = habits,
                    key = { habit -> habit.id }
                ) { habit ->
                    HabitItem(
                        habit = habit,
                        onLongClick = {
                            // Check if streak can be updated today
                            if (habit.lastStreakModified.toLocalDate() != LocalDateTime.now().toLocalDate()) {
                                viewModel.addOrUpdateHabit(
                                    habit.copy(
                                        curStreak = habit.curStreak + 1,
                                        highestStreak = maxOf(habit.curStreak + 1, habit.highestStreak),
                                        lastStreakModified = LocalDateTime.now(),
                                        acitivity = habit.acitivity + LocalDateTime.now().toLocalDate().toString()
                                    )
                                )
                            }
                        },
                        onClick = { navController.navigate("${Destinations.HABIT_DETAILS}/${habit.id}") },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitItem(
    habit: HabitEntity,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var isDeleting by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = habit.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Streak: ${habit.curStreak}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}