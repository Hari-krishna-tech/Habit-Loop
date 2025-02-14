package com.habitstreak.habitloop.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.utils.mapDayStringToCalendarDay
import com.habitstreak.habitloop.utils.scheduleReminder
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.widget.Toast

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val context = LocalContext.current;

    // notification permission handling
    fun scheduleReminders(context: Context) {
        if (isReminderSet && reminderTime != null) {
            val hour = reminderTime!!.hour
            val minute = reminderTime!!.minute
            frequency.forEach { dayString ->
                try {
                    val dayOfWeek = mapDayStringToCalendarDay(dayString)
                    scheduleReminder(context, dayOfWeek, hour, minute)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    val alarmPermission = Manifest.permission.SCHEDULE_EXACT_ALARM
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        if(isGranted) {
            scheduleReminders(context);
        } else {
            Toast.makeText(
                context,
                "Permission denied. Reminders might not work properly.",
                Toast.LENGTH_LONG
            ).show()
        }

        // navigate back after permission decision
        navController.popBackStack()
    }


    val predefinedHabits = listOf(
        PredefinedHabit("Morning Water", "💧", setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")),
        PredefinedHabit("Daily Steps", "👣", setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")),
        PredefinedHabit("Evening Journal", "📔", setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
        PredefinedHabit("Protein Intake", "🍗", setOf("Mon", "Wed", "Fri", "Sun")),
        PredefinedHabit("Digital Detox", "📵", setOf("Sat", "Sun")),
        PredefinedHabit("Language Practice", "🗣️", setOf("Tue", "Thu", "Sat")),
        PredefinedHabit("Posture Check", "🧘♂️", setOf("Mon", "Tue", "Wed", "Thu", "Fri")),
        PredefinedHabit("Vitamin Intake", "💊", setOf("Mon", "Wed", "Fri")),
        PredefinedHabit("Budget Review", "💰", setOf("Sun")),
        PredefinedHabit("Skin Care", "✨", setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")),
        PredefinedHabit("Learning Time", "🎓", setOf("Mon", "Tue", "Thu", "Fri")),
        PredefinedHabit("Meal Prep", "🥗", setOf("Sun")),
        PredefinedHabit("Gratitude Practice", "🙏", setOf("Mon", "Wed", "Fri")),
        PredefinedHabit("Strength Training", "🏋️♂️", setOf("Mon", "Wed", "Fri")),
        PredefinedHabit("Mindful Breathing", "🌬️", setOf("Tue", "Thu", "Sat")),
        PredefinedHabit("Email Cleanup", "📧", setOf("Fri")),
        PredefinedHabit("Family Time", "👨👩👧👦", setOf("Sat", "Sun")),
        PredefinedHabit("Creative Time", "🎨", setOf("Wed", "Sat"))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if(isCustomHabit) {
                    IconButton(
                        onClick = {

                             if(isCustomHabit) {
                                 isCustomHabit = false;
                             } else {
                                 navController.popBackStack()
                             }
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp),

                            )
                    } }
                },
                title = { Text(if (isCustomHabit) "Create Custom Habit" else "Choose Habit", modifier = Modifier.padding(horizontal = 8.dp)) },

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


                    FilledTonalButton(

                        onClick = {
                            isCustomHabit = true
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Create, contentDescription = "Create")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Custom Habit", fontWeight = FontWeight.Bold)
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
                    .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
               // Spacer(modifier = Modifier.height(16.dp))


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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "New Habit",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )


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


               FilledTonalButton(
                   onClick = {
                       if (title.isNotBlank() && frequency.isNotEmpty()) {
                           println("habit creation")
                           println(title)
                           println(emoji)
                           println(frequency.toList())
                           println(LocalDateTime.now().minusDays(1).toString())
                           println(isReminderSet)
                           println(reminderTime ?: LocalDateTime.now())

                           viewModel.addOrUpdateHabit(
                               HabitEntity(
                                   title = title,
                                   emoji = emoji,
                                   frequency = frequency.toList(),
                                   lastStreakModified = LocalDateTime.now().minusDays(1) ,
                                   isReminderSet = isReminderSet,
                                   reminderTime = reminderTime ?: LocalDateTime.now(),
                                   activity = emptyList()
                               )
                           )


                           // Schedule reminders if enabled

                           if(isReminderSet) {
                               val handlePermission: () -> Unit = {
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                       if (ContextCompat.checkSelfPermission(
                                               context,
                                               alarmPermission
                                           ) == PackageManager.PERMISSION_GRANTED
                                       ) {
                                           scheduleReminders(context)
                                           navController.popBackStack()
                                       } else {
                                           // Show permission dialog without popping back
                                           permissionLauncher.launch(alarmPermission)
                                       }
                                   } else {
                                       scheduleReminders(context)
                                       navController.popBackStack()
                                   }
                               }

                               // Execute the permission handling
                               handlePermission()
                           } else {
                               navController.popBackStack()
                           }
                       }
                   },
                   modifier = Modifier
                       .padding(16.dp)
                       .fillMaxWidth()
                       .height(48.dp),
                   shape = RoundedCornerShape(12.dp),
                   colors = ButtonDefaults.filledTonalButtonColors(
                       containerColor = MaterialTheme.colorScheme.primary,
                       contentColor = MaterialTheme.colorScheme.onPrimary
                   )
               ) {
                   Icon(Icons.Default.Done, contentDescription = "save")
                   Spacer(modifier = Modifier.width(8.dp))
                   Text("Save Habit", fontWeight = FontWeight.Bold)
               }
                Spacer(modifier = Modifier.height(16.dp))
             /*   Button(
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
                }*/
            }
        }
    }
}


data class PredefinedHabit(
    val title: String,
    val emoji: String,
    val frequency: Set<String>
)