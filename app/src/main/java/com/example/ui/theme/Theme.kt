package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = InkPrimary,
    onPrimary = InkOnPrimary,
    primaryContainer = InkPrimaryContainer,
    onPrimaryContainer = InkOnPrimaryContainer,
    secondary = SealSecondary,
    onSecondary = SealOnSecondary,
    secondaryContainer = SealSecondaryContainer,
    onSecondaryContainer = SealOnSecondaryContainer,
    tertiary = GoldTertiary,
    onTertiary = GoldOnTertiary,
    tertiaryContainer = GoldTertiaryContainer,
    onTertiaryContainer = GoldOnTertiaryContainer,
    background = PaperBackground,
    onBackground = InkTextPrimary,
    surface = IvorySurface,
    onSurface = InkTextPrimary,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = InkTextSecondary,
    outline = WarmBorder,
    outlineVariant = WarmBorder,
    error = DangerRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = TwilightBackground,
    onBackground = DarkTextPrimary,
    surface = TwilightSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = TwilightSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = TwilightBorder,
    outlineVariant = TwilightBorder,
    error = DarkSecondary
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun GreetingPromptTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
