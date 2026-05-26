package com.kasirku.owner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueDark,
    onPrimaryContainer = BlueLight,
    secondary = BlueLight,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = OnDarkText,
    surface = DarkSurface,
    onSurface = OnDarkText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkTextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF444466)
)

@Composable
fun OwnerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = OwnerTypography,
        shapes = OwnerShapes,
        content = content
    )
}
