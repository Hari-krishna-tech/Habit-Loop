package com.habitstreak.habitloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.Destinations
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack();
                        }){
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } ,
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
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
                            Text("Current Streak \uD83D\uDD25")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentHabit.highestStreak.toString(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text("Highest Streak \uD83D\uDD25")
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
                    // Updated reminder display
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("On ")

                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(currentHabit.reminderTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "")
                                }
                                append("\nOn days: ${currentHabit.frequency.joinToString()}")
                            }
                        )
                    }
                }

                // Activity Chart (GitHub-style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Activity Heatmap",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                      /*  val fakeActivity = listOf(
                            // Recent activity
                            "2024-10-23", "2024-10-20", "2024-10-18", "2024-10-16",
                            "2024-10-13", "2024-10-11", "2024-10-09",

                            // September activity
                            "2024-09-25", "2024-09-22", "2024-09-20", "2024-09-18",
                            "2024-09-15", "2024-09-13", "2024-09-11",

                            // August activity
                            "2024-08-28", "2024-08-25", "2024-08-23", "2024-08-21",
                            "2024-08-18", "2024-08-16", "2024-08-14",

                            // Random streaks
                            "2024-07-01", "2024-07-02", "2024-07-03", "2024-07-04",
                            "2024-05-15", "2024-05-16", "2024-05-17",

                            // Older activity
                            "2024-04-10", "2024-04-12", "2024-04-14",
                            "2024-03-05", "2024-03-07", "2024-03-09",
                            "2024-02-28", "2024-02-25", "2024-02-23",

                            // New Year resolution fails
                            "2025-01-01", "2025-01-02", "2025-01-22"
                        )

                        GitHubStyleActivityChart(
                                activityDates = fakeActivity,
                        frequencyDays = currentHabit.frequency,
                        modifier = Modifier.height(200.dp)
                        )*/
                        GitHubStyleActivityChart(
                            activityDates = currentHabit.activity,
                            frequencyDays = currentHabit.frequency,
                            modifier = Modifier.height(200.dp)
                        )


                    }
                }



            }
        }
    }
}


@Composable
private fun GitHubStyleActivityChart(
    activityDates: List<String>,
    frequencyDays: List<String>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusYears(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

    val activityDatesSet = activityDates.map { LocalDate.parse(it, dateFormatter) }.toSet()
    val frequencyDaysSet = frequencyDays.map { it.uppercase() }.toSet()

    val weeks = remember {
        val allWeeks = mutableListOf<List<LocalDate>>()
        var currentWeekStart = startDate

        while (currentWeekStart.isBefore(today) || currentWeekStart.isEqual(today)) {
            val weekDays = (0 until 7).map { currentWeekStart.plusDays(it.toLong()) }
            allWeeks.add(weekDays)
            currentWeekStart = currentWeekStart.plusWeeks(1)
        }
        allWeeks
    }

    Box(modifier = modifier) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(weeks) { week ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    week.forEach { date ->
                        val isFrequencyDay = frequencyDaysSet.contains(
                            dayFormatter.format(date).uppercase().take(3)
                        )
                        val isActive = activityDatesSet.contains(date)
                        val isFuture = date.isAfter(today)

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = when {
                                        isFuture -> Color.Transparent
                                        isActive -> Color(0xFF2E7D32) // GitHub-like green
                                        isFrequencyDay -> Color(0xFFF58F8F).copy(alpha = 0.6f) // GitHub's base color
                                        else -> Color(0xFF545456).copy(alpha = 0.3f) // Non-frequency days
                                    },
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

/*
package com.habitstreak.habitloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.Destinations
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack();
                        }){
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } ,
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
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
                            Text("Current Streak \uD83D\uDD25")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentHabit.highestStreak.toString(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text("Highest Streak \uD83D\uDD25")
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
                    // Updated reminder display
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("On ")

                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(currentHabit.reminderTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "")
                                }
                                append("\nOn days: ${currentHabit.frequency.joinToString()}")
                            }
                        )
                    }
                }

                // Activity Chart (GitHub-style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Activity Heatmap",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        GitHubStyleActivityChart(
                            activityDates = currentHabit.activity,
                            frequencyDays = currentHabit.frequency,
                            modifier = Modifier.height(200.dp)
                        )
                    }
                }



            }
        }
    }
}
*/

/*
@Composable
private fun GitHubStyleActivityChart(
    activityDates: List<String>,
    frequencyDays: List<String>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusYears(1)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

    val activityDatesSet = activityDates.map { LocalDate.parse(it, dateFormatter) }.toSet()
    val frequencyDaysSet = frequencyDays.map { it.uppercase() }.toSet()

    val weeks = remember {
        val totalDays = ChronoUnit.DAYS.between(startDate, today).toInt()
        val weeks = mutableListOf<List<LocalDate>>()
        var currentDate = startDate

        repeat(53) { // 52 weeks + possible partial week
            val weekDays = mutableListOf<LocalDate>()
            repeat(7) {
                if (currentDate.isBefore(today) || currentDate.isEqual(today)) {
                    weekDays.add(currentDate)
                    currentDate = currentDate.plusDays(1)
                }
            }
            if (weekDays.isNotEmpty()) weeks.add(weekDays)
        }
        weeks
    }

    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(weeks) { week ->
                week.forEach { date ->
                    val isFrequencyDay = frequencyDaysSet.contains(
                        dayFormatter.format(date).uppercase().take(3)
                    )
                    val isActive = activityDatesSet.contains(date)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = when {
                                    isActive -> Color(0xFF2E7D32) // Green for active days
                                    isFrequencyDay -> Color(0xFF544D4D) // Light grey for frequency days
                                    else -> Color(0xFF8A8A8A).copy(alpha = 0.3f) // Grey for non-frequency
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
} */
/*
@Composable
private fun GitHubStyleActivityChart(
    activityDates: List<String>,
    frequencyDays: List<String>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusYears(1)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

    val activityDatesSet = activityDates.map { LocalDate.parse(it, dateFormatter) }.toSet()
    val frequencyDaysSet = frequencyDays.map { it.uppercase() }.toSet()

    val weeks = remember {
        val totalDays = ChronoUnit.DAYS.between(startDate, today).toInt()
        val weeks = mutableListOf<List<LocalDate>>()
        var currentDate = startDate

        repeat(53) { // 52 weeks + possible partial week
            val weekDays = mutableListOf<LocalDate>()
            repeat(7) {
                if (currentDate.isBefore(today) || currentDate.isEqual(today)) {
                    weekDays.add(currentDate)
                    currentDate = currentDate.plusDays(1)
                }
            }
            if (weekDays.isNotEmpty()) weeks.add(weekDays)
        }
        weeks
    }

    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(weeks) { week ->
                week.forEach { date ->
                    val isFrequencyDay = frequencyDaysSet.contains(
                        dayFormatter.format(date).uppercase().take(3)
                    )
                    val isActive = activityDatesSet.contains(date)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = when {
                                    isActive -> Color(0xFF2E7D32) // Green for active days
                                    isFrequencyDay -> Color(0xFF544D4D) // Light grey for frequency days
                                    else -> Color(0xFF8A8A8A).copy(alpha = 0.3f) // Grey for non-frequency
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

 */