package com.ivanna.oneweather.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun WeatherIcon(
    weatherCode: Int,
    isDay: Boolean = true,
    fontSize: TextUnit = 42.sp,
    modifier: Modifier = Modifier
) {
    val icon = when (weatherCode) {
        0 -> if (isDay) "☀️" else "🌙"
        1 -> if (isDay) "🌤️" else "🌙"
        2 -> "⛅"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55 -> "🌦️"
        56, 57 -> "🌧️"
        61, 63, 65 -> "🌧️"
        66, 67 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "🌧️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "🌡️"
    }

    Text(
        text = icon,
        fontSize = fontSize,
        modifier = modifier
    )
}