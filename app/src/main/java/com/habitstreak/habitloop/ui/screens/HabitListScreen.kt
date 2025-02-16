package com.habitstreak.habitloop.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.Destinations
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@SuppressLint("NewApi")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    val habits by viewModel.allHabits.collectAsState()


    // check and rest streaks when screen loads

    LaunchedEffect(habits) {
        habits.forEach{
            habit ->
                checkAndResetStreak(habit, viewModel);
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Destinations.CREATE_HABIT) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("New Habit") },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    ) { padding ->
        if (habits.isEmpty()) {
          EmptyStateUI(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = habits,
                    key = { habit -> habit.id }
                ) { habit ->
                   SwipeToDeleteContainer(
                       onDelete = {viewModel.deleteHabit(habit)}
                   ) {
                       HabitItem(habit = habit, onLongClick = {
                           tryUpdateStreak(habit, viewModel)
                       }, onClick = {
                           navController.navigate("${Destinations.HABIT_DETAILS}/${habit.id}")
                       })
                   }
                }
            }
        }
    }
}
@Composable
private fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    val swipeThreshold = with(LocalDensity.current) { 200.dp.toPx() } // Increased threshold
    val animatedOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = tween(durationMillis = 400), // Slower animation
        label = "swipeAnimation"
    )

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf(false) }

    if (showConfirmationDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete()
                showConfirmationDialog = false
            },
            onDismiss = {
                showConfirmationDialog = false
                pendingDelete = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        // Delete background with warning
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp), // More padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Delete Warning",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        }

        // Swipeable content with resistance
        Box(
            modifier = Modifier
                .offset(x = animatedOffset.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(swipeOffset) > swipeThreshold) {
                                pendingDelete = true
                                showConfirmationDialog = true
                            } else {
                                swipeOffset = 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            // Add resistance (40% of drag amount)
                            swipeOffset = (swipeOffset + dragAmount * 0.7f)
                                .coerceIn(-swipeThreshold * 1.5f, 0f)
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("This action cannot be undone. Delete this habit?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmptyStateUI(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✨ No Habits Yet!",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Long press habits to update streaks",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun checkAndResetStreak(habit: HabitEntity, viewModel: HabitViewModel) {
    val today = LocalDate.now()
    val lastModified = habit.lastStreakModified.toLocalDate()

    // Get the most recent frequency day before today
    val lastFrequencyDay = findLastFrequencyDay(today, habit.frequency)

    // If last modified date is before the last frequency day, reset streak
    if (lastModified.isBefore(lastFrequencyDay)) {
        viewModel.addOrUpdateHabit(
            habit.copy(
                curStreak = 0,
                //lastStreakModified = LocalDateTime.now()
            )
        )
    }
}


private fun findLastFrequencyDay(today: LocalDate, frequency: List<String>): LocalDate {
    var checkDate = today

    // Look back up to 7 days to find the last frequency day

    for (i in 0..7) {
        val dayName = checkDate.dayOfWeek.name
            .take(3)
            .lowercase()
            .replaceFirstChar { it.uppercase() }

        if (dayName in frequency) {
            return checkDate
        }
        checkDate = checkDate.minusDays(1)
    }

    // If no frequency day found in last week, return today
    return today
}


@Composable
private fun StreakCounter(current: Int, highest: Int, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BadgedBox(
            badge = {
                Badge {
                    Text(highest.toString())
                }
            }
        ) {
            Text(
                text = current.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (enabled) "Tap & hold to update" else "Not today",
            style = MaterialTheme.typography.labelSmall
        )
    }
}


private fun tryUpdateStreak(habit: HabitEntity, viewModel: HabitViewModel) {
    val today = LocalDate.now()
    val lastModified = habit.lastStreakModified.toLocalDate()
    val daysSinceLast = ChronoUnit.DAYS.between(lastModified, today)

    // Format current day to match frequency format
    val currentDayFormatted = today.dayOfWeek.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    // Check if today is a frequency day and hasn't been updated in the last 24 hours
    println("Habit Frequency       Habit Frequency " )
    println(habit.frequency)
    if (currentDayFormatted in habit.frequency && daysSinceLast >= 1) {
        // If we missed the last frequency day, reset streak
        val lastFrequencyDay = findLastFrequencyDay(today.minusDays(1), habit.frequency)
        if (lastModified.isBefore(lastFrequencyDay)) {
            viewModel.addOrUpdateHabit(
                habit.copy(
                    curStreak = 1, // Reset and start new streak
                    lastStreakModified = LocalDateTime.now(),
                    activity = habit.activity + today.toString()
                )
            )
        } else {
            // Continue streak
            viewModel.addOrUpdateHabit(
                habit.copy(
                    curStreak = habit.curStreak + 1,
                    highestStreak = maxOf(habit.highestStreak, habit.curStreak + 1),
                    lastStreakModified = LocalDateTime.now(),
                    activity = habit.activity + today.toString()
                )
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitItem(
    habit: HabitEntity,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val today = LocalDate.now()
    val lastModified = habit.lastStreakModified.toLocalDate()
    val daysSinceLast = ChronoUnit.DAYS.between(lastModified, today)
    // Convert day name to match frequency format ("Mon", "Tue")
    val currentDayFormatted = today.dayOfWeek.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }
    val canUpdate = daysSinceLast >= 1 && currentDayFormatted in habit.frequency


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { if (canUpdate) onLongClick() }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = habit.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "${habit.curStreak}🔥",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}