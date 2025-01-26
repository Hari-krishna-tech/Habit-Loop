package com.habitstreak.habitloop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var isReminderSet by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<LocalDateTime?>(null) }

    LaunchedEffect(habitId) {
        habit = habitId?.let { viewModel.getHabitById(it) }
        if (habit == null) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        title = habit?.title ?: ""
        emoji = habit?.emoji ?: "😊"
        frequency = habit?.frequency?.toSet() ?: setOf()
       isReminderSet = habit?.isReminderSet ?: false

        reminderTime = habit?.reminderTime ?: null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Habit") },
                navigationIcon = {
                    IconButton(onClick = {
                       navController.popBackStack();
                    }){
                       Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {


                                habit?.let { currentHabit ->
                                    viewModel.addOrUpdateHabit(
                                        currentHabit.copy(
                                            title = title,
                                            emoji = emoji,
                                            frequency = frequency.toList(),
                                            isReminderSet = isReminderSet,
                                            reminderTime = reminderTime ?: LocalDateTime.now()
                                        )
                                    )
                                }
                                navController.popBackStack()
                        },
                        enabled = title.isNotBlank() && frequency.isNotEmpty() && (isReminderSet && reminderTime != null)
                    ) {
                        Text("Save", modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.surface )
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Habit Title") },
                        leadingIcon = {
                            Icon(Icons.Default.Create, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji") },
                        leadingIcon = {
                            Text("😊", modifier = Modifier.size(24.dp));
                        } ,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                }

           // frequency selector

            // frequency selector
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Frequency", style = MaterialTheme.typography.titleMedium)
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val animatedProgress = remember { Animatable(0f) }

                    LaunchedEffect(frequency) {
                        animatedProgress.animateTo(1f)
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEach { day ->
                            val selected = frequency.contains(day)
                            val backgroundColor by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                animationSpec = tween(300), label = ""
                            )

                            val borderColor by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                animationSpec = tween(300), label = ""
                            )

                            val textColor by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                animationSpec = tween(300), label = ""
                            )

                            val scale by animateFloatAsState(
                                targetValue = if (selected) 1.1f else 1f,
                                animationSpec = spring(stiffness = Spring.StiffnessLow), label = ""
                            )

                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .background(backgroundColor)
                                    .clickable {
                                        frequency = if (selected) {
                                            frequency - day
                                        } else {
                                            frequency + day
                                        }
                                    }
                                    .animateContentSize()
                                    .scale(scale)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = textColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Text(
                                        text = day.take(3),
                                        color = textColor,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // reminder selector

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {


// Replace the existing reminder section with this code
                    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
                    val showTimePicker = remember { mutableStateOf(false) }
                    val timePickerState = rememberTimePickerState(
                        initialHour = LocalTime.now().hour,
                        initialMinute = LocalTime.now().minute
                    )

// Reminder Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { isReminderSet = !isReminderSet }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val reminderColor by animateColorAsState(
                            if (isReminderSet) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                            label = ""
                        )

                        Checkbox(
                            checked = isReminderSet,
                            onCheckedChange = { isReminderSet = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Text(
                            text = "Daily Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isReminderSet) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Reminder Set",
                                tint = reminderColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

// Time Display/Picker
                    if (isReminderSet) {
                        val currentTime = remember { LocalTime.now() }
                        val formattedTime = remember(reminderTime) {
                            reminderTime?.toLocalTime()?.format(timeFormatter) ?: "Select Time"
                        }

                        val backgroundColor by animateColorAsState(
                            if (reminderTime != null) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            label = ""
                        )

                        val textColor by animateColorAsState(
                            if (reminderTime != null) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = ""
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(backgroundColor)
                                .clickable { showTimePicker.value = true }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = "Time",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )

                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

// Time Picker Dialog
                    if (showTimePicker.value) {
                        AlertDialog(
                            onDismissRequest = { showTimePicker.value = false },
                            title = { Text("Select Reminder Time") },
                            text = {
                                Column(
                                    modifier = Modifier.padding(top = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TimePicker(
                                        state = timePickerState,
                                        colors = TimePickerDefaults.colors(
                                            clockDialColor = MaterialTheme.colorScheme.primary,
                                            selectorColor = MaterialTheme.colorScheme.primary,
                                            clockDialUnselectedContentColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val now = LocalDateTime.now()
                                        reminderTime = now
                                            .withHour(timePickerState.hour)
                                            .withMinute(timePickerState.minute)
                                        showTimePicker.value = false
                                    }
                                ) {
                                    Text("Set Time")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showTimePicker.value = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }


            /*
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
                        } */
        }
    }
}