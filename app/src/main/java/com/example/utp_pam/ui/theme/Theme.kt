package com.example.utp_pam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SambalGold,
    secondary = ChiliRedSoft,
    tertiary = LeafGreen,
    background = NightInk,
    surface = Color(0xFF2E292A),
    surfaceContainerHigh = Color(0xFF3A3335),
    surfaceContainerLowest = Color(0xFF1E1A1B),
    onPrimary = NightInk,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF8EFE6),
    onSurface = Color(0xFFF8EFE6),
    onSurfaceVariant = Color(0xFFD8CBBE),
    primaryContainer = ChiliRed,
    onPrimaryContainer = Color(0xFFFFE7D8),
    outlineVariant = Color(0xFF5B5254),
    inverseSurface = Color(0xFFF8EFE6),
    inverseOnSurface = NightInk
)

private val LightColorScheme = lightColorScheme(
    primary = ChiliRed,
    secondary = ChiliRedSoft,
    tertiary = LeafGreen,
    background = CreamBase,
    surface = Color.White,
    surfaceContainerHigh = Color(0xFFF2E7D8),
    surfaceContainerLowest = WarmSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = NightInk,
    onSurface = NightInk,
    onSurfaceVariant = ClayBrown,
    primaryContainer = Color(0xFFF5D8B4),
    onPrimaryContainer = NightInk,
    outlineVariant = Color(0xFFD6C6B8),
    inverseSurface = NightInk,
    inverseOnSurface = CreamBase
)

@Composable
fun UTP_PAMTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
