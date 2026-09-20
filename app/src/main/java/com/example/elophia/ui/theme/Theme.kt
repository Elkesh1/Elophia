package com.example.elophia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
// LIGHT COLOR SCHEME
// Background: cream
// Surfaces: light cream (bars, dialogs, default cards)
// Hero cards: navy via primaryContainer
// Buttons: gold
// ═══════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    // Primary = gold
    primary = Gold,
    onPrimary = TextOnGold,
    primaryContainer = NavyCard,
    onPrimaryContainer = TextOnNavy,

    // Secondary = navy accent
    secondary = NavyCard,
    onSecondary = TextOnNavy,
    secondaryContainer = NavyLight,
    onSecondaryContainer = TextOnNavy,

    // Tertiary = deeper gold
    tertiary = GoldDark,
    onTertiary = TextOnGold,

    // Background = cream
    background = CreamBackground,
    onBackground = Navy,

    // Surface = cream (for bars, dialogs, default cards)
    surface = CreamSurface,
    onSurface = Navy,
    surfaceVariant = CreamBackground,
    onSurfaceVariant = TextSecondaryLight,

    // Outline
    outline = GoldBorder,
    outlineVariant = DividerLight,

    // Status
    error = ErrorRed,
    onError = Color.White
)

// ═══════════════════════════════════════════════
// DARK COLOR SCHEME
// Background: deep navy
// Surfaces: darker navy (bars, dialogs, default cards)
// Hero cards: navy via primaryContainer
// Buttons: gold
// ═══════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    // Primary = gold
    primary = Gold,
    onPrimary = TextOnGold,
    primaryContainer = NavyCard,
    onPrimaryContainer = TextOnNavy,

    // Secondary = navy
    secondary = NavyCard,
    onSecondary = TextOnNavy,
    secondaryContainer = NavyLight,
    onSecondaryContainer = TextOnNavy,

    // Tertiary = lighter gold
    tertiary = GoldLight,
    onTertiary = TextOnGold,

    // Background = deep navy
    background = DarkBackground,
    onBackground = TextPrimaryDark,

    // Surface = slightly lighter navy for cards & bars
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondaryDark,

    // Outline
    outline = GoldBorder,
    outlineVariant = DividerDark,

    // Status
    error = ErrorRedDark,
    onError = Color.White
)

@Composable
fun ElophiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}