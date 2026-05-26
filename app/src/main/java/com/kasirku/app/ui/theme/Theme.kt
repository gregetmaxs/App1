package com.kasirku.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealDark,
    onPrimaryContainer = TealLight,
    secondary = TealLight,
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
fun KasirKuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = KasirKuTypography,
        shapes = KasirKuShapes,
        content = content
    )
}
