package com.nashirrestafauzian0083.todolist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NightGreen,
    onPrimary = DeepForest,
    secondary = SoftNightPeach,
    onSecondary = DeepForest,
    tertiary = SoftMint,
    background = DeepForest,
    onBackground = LightMist,
    surface = DarkSurface,
    onSurface = LightMist,
    primaryContainer = DarkSurface,
    onPrimaryContainer = LightMist,
    secondaryContainer = SoftNightPeach,
    onSecondaryContainer = DeepForest,
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreen,
    onPrimary = White,
    secondary = WarmPeach,
    onSecondary = DarkText,
    tertiary = SoftMint,
    background = WarmBackground,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    primaryContainer = SoftMint,
    onPrimaryContainer = DarkText,
    secondaryContainer = WarmPeach,
    onSecondaryContainer = DarkText,
)

@Composable
fun TodoListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
