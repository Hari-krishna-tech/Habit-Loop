package com.habitstreak.habitloop.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.habitstreak.habitloop.data.viewmodel.ThemeViewModel
import com.habitstreak.habitloop.ui.theme.HabitLoopTheme

@Composable
fun ThemeWrapper(
    viewModel: ThemeViewModel = ThemeViewModel(
        dataStore = TODO()
    ),
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val userPreference by viewModel.themeState.collectAsState()

    val isDarkMode = when (userPreference) {
        true -> true
        false -> false
        null -> systemDarkTheme
        else -> {}
    }

    HabitLoopTheme (
        colors = if (isDarkMode) DarkColors else LightColors
    ) {
        content()
    }
}