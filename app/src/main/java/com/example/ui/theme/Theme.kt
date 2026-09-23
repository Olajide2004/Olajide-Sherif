package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SignalGold,
    onPrimary = InkLight,
    primaryContainer = KeycapDark,
    onPrimaryContainer = SignalGold,
    secondary = SteelDark,
    onSecondary = PaperDark,
    background = PaperDark,
    onBackground = InkDark,
    surface = KeycapDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = InkDark,
    error = AlertRed,
    onError = PaperLight,
    outline = SteelDark
)

private val LightColorScheme = lightColorScheme(
    primary = SignalGoldDark,
    onPrimary = PaperLight,
    primaryContainer = KeycapLight,
    onPrimaryContainer = InkLight,
    secondary = SteelLight,
    onSecondary = PaperLight,
    background = PaperLight,
    onBackground = InkLight,
    surface = KeycapLight,
    onSurface = InkLight,
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = InkLight,
    error = AlertRed,
    onError = PaperLight,
    outline = SteelLight
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    },
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

