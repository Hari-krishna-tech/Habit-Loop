package com.habitstreak.habitloop.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.JsonSyntaxException
import com.habitstreak.habitloop.data.database.HabitEntity
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.time.LocalDateTime

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: HabitViewModel,
    onDarkModeChanged: (Boolean) -> Unit
    ) {

    val isSystemDark = isSystemInDarkTheme()
    val context = LocalContext.current
    var darkMode by remember { mutableStateOf(isSystemDark) }
    var isImporting by remember { mutableStateOf(false) }


    // Theme info
    //val currentTheme = if (darkMode) DarkColorScheme else LightColorScheme
    // Json Export/ import Logic

    if (isImporting) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }


    val scope = rememberCoroutineScope()

    // Move launchers to composable
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val gson = GsonBuilder()
                        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
                        .create()
                    val habits = viewModel.allHabits.first() // Now in coroutine
                    val json = gson.toJson(habits)
                    println("export screen")
                    println(json)
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    isImporting =true;
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val json = inputStream.bufferedReader().use { it.readText() }
//                        val habits = Gson().fromJson(json, Array<HabitEntity>::class.java).toList()
//                        if(habits.isNullOrEmpty()) {
//                            Toast.makeText(context, "Invalid data format", Toast.LENGTH_SHORT).show()
//                            return@launch
//                        }
                        val habits = safeParseHabits(json) ?: run {
                            Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        viewModel.importHabits(habits) // Now in coroutine
                        Toast.makeText(context, "Habits imported successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isImporting = false
                }

            }
        }
    }


   // val jsonHandler = remember { JsonHandler(context, viewModel) }

    Scaffold(
topBar = {
    TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
            IconButton(onClick  = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }
    )
}
) { padding ->
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dark Mode Toggle
        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = darkMode,
                    onCheckedChange = { it ->
                        run {
                            darkMode = it;
                            onDarkModeChanged(it)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        // Export Button
        SettingsButton(
            text = "Export Habits",
            onClick = { exportLauncher.launch("habits_${System.currentTimeMillis()}.json")}
        )

        // Import Button
        SettingsButton(
            text = "Import Habits",
            onClick = { importLauncher.launch("application/json") }
        )
    }
}

}

@Composable
private fun SettingsButton(text: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun safeParseHabits(json: String): List<HabitEntity>? {
    return try {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()

        gson.fromJson(json, Array<HabitEntity>::class.java)?.toList()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString() ?: "")
    }


}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return try {
            LocalDateTime.parse(json?.asString)
        } catch (e: Exception) {
            LocalDateTime.now().minusDays(1) // Fallback for invalid dates
        }
    }
}

/*
class JsonHandler(private val context: Context, private val viewModel: HabitViewModel) {
    private val gson = Gson()
    private var exportedUri by mutableStateOf<Uri?>(null)

    // Export logic
    private val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val habits = viewModel.allHabits.first();
                    val json = gson.toJson(habits)
                    os.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Import logic
    private val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val habits = gson.fromJson(json, Array<HabitEntity>::class.java).toList()
                    viewModel.importHabits(habits)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun exportHabits() {
        exportLauncher.launch("habits_${System.currentTimeMillis()}.json")
    }

    fun importHabits() {
        importLauncher.launch("application/json")
    }
} */