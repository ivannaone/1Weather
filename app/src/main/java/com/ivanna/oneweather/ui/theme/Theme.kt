package com.ivanna.oneweather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E9FF),
    onPrimaryContainer = Color(0xFF003258),
    secondary = Color(0xFF287DBB),
    onSecondary = Color.White,
    tertiary = Color(0xFF00A6C7),
    background = Color(0xFFF0F7FF),
    onBackground = Color(0xFF102A43),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF102A43),
    surfaceVariant = Color(0xFFDCEBFA),
    onSurfaceVariant = Color(0xFF526B84),
    outline = Color(0xFF8BAAC8)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF82B9FF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF174D80),
    onPrimaryContainer = Color(0xFFD6E9FF),
    secondary = Color(0xFF8CC7F5),
    onSecondary = Color(0xFF00344F),
    tertiary = Color(0xFF65D5E8),
    background = Color(0xFF081827),
    onBackground = Color(0xFFE4F1FF),
    surface = Color(0xFF10263A),
    onSurface = Color(0xFFE4F1FF),
    surfaceVariant = Color(0xFF1B3852),
    onSurfaceVariant = Color(0xFFB5CDE2),
    outline = Color(0xFF587895)
)

@Composable
fun _1WeatherTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
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