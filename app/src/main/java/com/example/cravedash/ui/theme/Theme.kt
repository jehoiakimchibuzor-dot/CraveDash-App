package com.example.cravedash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.cravedash.AppTheme
import com.example.cravedash.ThemeManager

private val LightScheme = lightColorScheme(
    primary             = CraveOrange,
    onPrimary           = Color.White,
    primaryContainer    = CraveOrangeLight,
    background          = LightBackground,
    onBackground        = LightOnBg,
    surface             = LightSurface,
    onSurface           = LightOnSurface,
    surfaceVariant      = LightCard,
    outline             = LightDivider,
    secondary           = CraveOrangeDark,
    onSecondary         = Color.White
)

private val DarkScheme = darkColorScheme(
    primary             = CraveOrange,
    onPrimary           = Color.White,
    primaryContainer    = Color(0xFF3D2200),
    background          = DarkBackground,
    onBackground        = DarkOnBg,
    surface             = DarkSurface,
    onSurface           = DarkOnSurface,
    surfaceVariant      = DarkCard,
    outline             = DarkDivider,
    secondary           = CraveOrangeDark,
    onSecondary         = Color.White
)

@Composable
fun CraveDashTheme(content: @Composable () -> Unit) {
    // Resolve whether we're actually in dark mode
    val systemDark = isSystemInDarkTheme()
    val dark = when (ThemeManager.theme) {
        AppTheme.LIGHT  -> false
        AppTheme.DARK   -> true
        AppTheme.SYSTEM -> systemDark
    }
    // Write back so every screen can read ThemeManager.isDarkMode as a plain Boolean
    ThemeManager.isDarkMode = dark

    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        typography  = Typography,
        content     = content
    )
}
