package com.habitstreak.habitloop.utils

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.habitstreak.habitloop.data.viewmodel.ThemeViewModel
import com.habitstreak.habitloop.ui.theme.HabitLoopTheme

@Composable
fun ThemeWrapper(
    viewModel: ThemeViewModel = ThemeViewModel(LocalContext.current.applicationContext.dataStore),
    content: @Composable () -> Unit
) {
    val themeState by viewModel.themeState.collectAsState()
    val isDarkMode = when (themeState) {
        true -> true
        false -> false
        null -> isSystemInDarkTheme()
    }

    HabitLoopTheme(darkTheme = isDarkMode) {
        content()
    }
}