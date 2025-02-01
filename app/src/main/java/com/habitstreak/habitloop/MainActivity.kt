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
import com.habitstreak.habitloop.utils.ThemeWrapper


val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemeWrapper  {
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
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

