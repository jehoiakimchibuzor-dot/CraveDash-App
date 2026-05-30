package com.example.cravedash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Which theme the user has chosen. */
enum class AppTheme { LIGHT, DARK, SYSTEM }

/**
 * Global theme state.
 *  - [theme]      — the user's preference (LIGHT / DARK / SYSTEM)
 *  - [isDarkMode] — the resolved boolean (computed in CraveDashTheme
 *                   and written back here so all screens can read it
 *                   without needing a @Composable context)
 */
object ThemeManager {
    var theme     by mutableStateOf(AppTheme.SYSTEM)
    var isDarkMode by mutableStateOf(false)
}
