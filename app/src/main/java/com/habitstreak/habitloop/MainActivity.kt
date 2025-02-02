package com.habitstreak.habitloop

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.habitstreak.habitloop.data.database.AppDatabase
import com.habitstreak.habitloop.data.repository.HabitRepository
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.navigation.BottomNavBar
import com.habitstreak.habitloop.navigation.NavGraph
import com.habitstreak.habitloop.ui.theme.HabitLoopTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) };
            HabitLoopTheme(darkTheme = isDarkMode)  {
                val navController = rememberNavController()

                val habitDao = AppDatabase.getInstance(applicationContext).habitDao();
                val habitRepository = HabitRepository(habitDao);
                val viewModel = HabitViewModel(habitRepository);


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                        onDarkModeChanged = { isDarkMode = it }
                    )
                }
            }
        }
    }
}


