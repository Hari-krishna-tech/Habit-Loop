package com.habitstreak.habitloop.data.viewmodel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ThemeViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    // Expose three states: null = system default
    val themeState = dataStore.data
        .map { prefs -> prefs[darkModeKey] }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateThemePreference(useDark: Boolean?) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                if (useDark != null) {
                    prefs[darkModeKey] = useDark
                } else {
                    prefs.remove(darkModeKey)
                }
            }
        }
    }
}
