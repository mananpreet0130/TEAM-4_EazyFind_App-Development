package com.example.eazyfind.ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(

    // Brand Primary
    primary = PrimaryColor,
    onPrimary = Color.White,

    // Secondary / Accent (Gold)
    secondary = GoldAccent,
    onSecondary = DarkText,

    // App Background
    background = AppBackground,
    onBackground = DarkText,

    // Surfaces
    surface = AppBackground,
    onSurface = DarkText,

    // Success / Offers
    tertiary = SuccessGreen,
    onTertiary = Color.White,

    // Errors
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun EazyFindTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}