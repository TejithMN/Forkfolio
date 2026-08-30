package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = CreamSurfaceVariant,
    onSecondaryContainer = EspressoDark,
    tertiary = GoldAccent,
    onTertiary = Color(0xFFFFFFFF),
    background = CreamBackground,
    onBackground = EspressoDark,
    surface = CreamSurface,
    onSurface = EspressoDark,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = EspressoMedium,
    outline = GoldBorder,
    outlineVariant = GoldBorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = Color(0xFF261D05),
    primaryContainer = Color(0xFF57410A),
    onPrimaryContainer = GoldContainer,
    secondary = GoldAccent,
    onSecondary = Color(0xFF2A1F08),
    secondaryContainer = Color(0xFF382F24),
    onSecondaryContainer = GoldContainer,
    tertiary = GoldPrimary,
    onTertiary = Color(0xFF2A1F08),
    background = Color(0xFF1C1712),
    onBackground = Color(0xFFF7F1E6),
    surface = Color(0xFF241E18),
    onSurface = Color(0xFFF7F1E6),
    surfaceVariant = Color(0xFF332B23),
    onSurfaceVariant = Color(0xFFD7C9B8),
    outline = Color(0xFF8A7342),
    outlineVariant = Color(0xFF4C3E25)
)

@Composable
fun ForkfolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Preserve intentional luxurious cream & gold theme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

