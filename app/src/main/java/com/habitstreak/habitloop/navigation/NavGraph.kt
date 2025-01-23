package com.habitstreak.habitloop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.habitstreak.habitloop.data.viewmodel.HabitViewModel
import com.habitstreak.habitloop.ui.screens.HabitListScreen;
import com.habitstreak.habitloop.ui.screens.CreateHabitScreen;
import com.habitstreak.habitloop.ui.screens.HabitDetailsScreen;
import com.habitstreak.habitloop.ui.screens.EditHabitScreen;

object Destinations {
    const val HABIT_LIST = "habit_list"
    const val CREATE_HABIT = "create_habit"
    const val SETTINGS = "settings"
    const val HABIT_DETAILS = "habit_details"
    const val EDIT_HABIT = "edit_habit"

    // Arguments
    const val HABIT_ID = "habitId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Destinations.HABIT_LIST,
    modifier: Modifier = Modifier,
    viewModel: HabitViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.HABIT_LIST) {
            HabitListScreen(navController = navController, viewModel = viewModel)
        }

        composable(Destinations.CREATE_HABIT) {
            CreateHabitScreen(navController = navController, viewModel = viewModel)
        }

        composable(Destinations.SETTINGS) {
            // SettingsScreen will be implemented later
        }

        composable(
            route = "${Destinations.HABIT_DETAILS}/{${Destinations.HABIT_ID}}",
            arguments = listOf(navArgument(Destinations.HABIT_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt(Destinations.HABIT_ID)
            HabitDetailsScreen(habitId = habitId, navController = navController, viewModel = viewModel)
        }

        composable(
            route = "${Destinations.EDIT_HABIT}/{${Destinations.HABIT_ID}}",
            arguments = listOf(navArgument(Destinations.HABIT_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt(Destinations.HABIT_ID)
            EditHabitScreen(habitId = habitId, navController = navController, viewModel = viewModel)
        }
    }
}