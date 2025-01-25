package com.habitstreak.habitloop.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: @Composable () -> Unit
)


@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem(
            title = "Habits",
            route = Destinations.HABIT_LIST,
            icon = { Icon(Icons.Default.List, contentDescription = "Habits") }
        ),
        BottomNavItem(
            title = "Create",
            route = Destinations.CREATE_HABIT,
            icon = { Icon(Icons.Default.Add, contentDescription = "Create Habit") }
        ),
        BottomNavItem(
            title = "Settings",
            route = Destinations.SETTINGS,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { item.icon() },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Destinations.HABIT_LIST) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}