package com.habitstreak.habitloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitstreak.habitloop.ui.theme.HabitLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination

            val bottomBarDestinations = listOf(
                Screen.HabitList,
                Screen.CreationPresetList,
                Screen.Settings
            )

            HabitLoopTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {Text(getScreenTitle(currentDestination?.route))}
                        )
                    },

                    bottomBar = {
                        if(bottomBarDestinations.any { it.route == currentDestination?.route }) {
                            BottomNavigationBar(
                                navController = navController,
                                currentDestination = currentDestination
                            )
                        }
                    }
                    ) { innerPadding ->
                    // need to code tomorrow for sure
                    NavHost(
                        navController = navController,
                        startDestination = Screen.HabitList.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Main bottom navigation screens
                        composable(Screen.HabitList.route) {
                            HabitListScreen(navController)
                        }
                        composable(Screen.CreationPresetList.route) {
                            CreationPresetListScreen(navController)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen()
                        }

                        // Nested navigation screens
                        composable(Screen.HabitCreate.route) {
                            HabitCreateScreen(navController)
                        }
                        composable(Screen.HabitDetail.route) {
                            HabitDetailScreen(navController)
                        }
                        composable(Screen.HabitEdit.route) {
                            HabitEditScreen(navController)
                        }
                    }
                }
            }
        }
    }
}


