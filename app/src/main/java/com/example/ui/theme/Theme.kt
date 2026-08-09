package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RoyalCasinoColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldSecondary,
    onPrimaryContainer = Color.Black,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = CasinoSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonGreen,
    onTertiary = Color.Black,
    background = CasinoDarkBackground,
    onBackground = TextPrimary,
    surface = CasinoSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CasinoSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CasinoCardBorder,
    error = NeonRed,
    onError = Color.White
)

@Composable
fun RoyalCasinoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RoyalCasinoColorScheme,
        typography = Typography,
        content = content
    )
}
