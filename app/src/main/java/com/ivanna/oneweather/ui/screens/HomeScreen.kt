package com.ivanna.oneweather.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivanna.oneweather.ui.components.TemperatureChart
import com.ivanna.oneweather.ui.components.WeatherIcon
import com.ivanna.oneweather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    cityName: String,
    latitude: Double,
    longitude: Double,
    selectedLanguage: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val isEnglish = selectedLanguage == "English"

    LaunchedEffect(latitude, longitude) {
        weatherViewModel.loadWeather(latitude, longitude)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "1Weather",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )

                    Text(
                        text = if (isEnglish) "Weather forecast" else "Prakiraan cuaca",
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onSearchClick) {
                        Text(if (isEnglish) "Search" else "Cari")
                    }

                    IconButton(onClick = onSettingsClick) {
                        Text(
                            text = "⚙",
                            fontSize = 23.sp,
                            color = colors.primary
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = cityName,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )

                    Text(
                        text = if (isEnglish) "Current Weather" else "Cuaca Saat Ini",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = colors.onPrimary.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    if (uiState.isLoading && uiState.weather == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = colors.onPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isEnglish) "Loading weather..." else "Memuat cuaca...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = colors.onPrimary
                        )
                    }

                    uiState.weather?.let { weather ->
                        val current = weather.current

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier.width(112.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnimatedWeatherIcon(
                                    weatherCode = current.weatherCode,
                                    isDay = current.isDay == 1,
                                    fontSize = 66.sp
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = getWeatherDescription(
                                        current.weatherCode,
                                        isEnglish
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.onPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${current.temperature.toInt()}°",
                                    fontSize = 68.sp,
                                    fontWeight = FontWeight.Light,
                                    color = colors.onPrimary,
                                    maxLines = 1
                                )

                                Text(
                                    text = if (isEnglish) {
                                        "Feels like ${current.feelsLike.toInt()}°C"
                                    } else {
                                        "Terasa ${current.feelsLike.toInt()}°C"
                                    },
                                    fontSize = 12.sp,
                                    color = colors.onPrimary.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(
                            color = colors.onPrimary.copy(alpha = 0.25f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeroWeatherDetail(
                                icon = "💧",
                                label = if (isEnglish) "Humidity" else "Kelembapan",
                                value = "${current.humidity}%"
                            )

                            HeroWeatherDetail(
                                icon = "💨",
                                label = if (isEnglish) "Wind" else "Angin",
                                value = String.format(
                                    Locale.US,
                                    "%.1f km/h",
                                    current.windSpeed
                                )
                            )

                            HeroWeatherDetail(
                                icon = "☀️",
                                label = if (isEnglish) "UV Index" else "Indeks UV",
                                value = String.format(
                                    Locale.US,
                                    "%.1f",
                                    current.uvIndex
                                )
                            )
                        }
                    }

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = uiState.error ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.onPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )

                        Button(
                            onClick = {
                                weatherViewModel.loadWeather(latitude, longitude)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surface,
                                contentColor = colors.primary
                            )
                        ) {
                            Text(if (isEnglish) "Try Again" else "Coba Lagi")
                        }
                    }

                    if (uiState.isLoading && uiState.weather != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.onPrimary,
                            trackColor = colors.onPrimary.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.lastUpdated?.let { time ->
                                SimpleDateFormat(
                                    "HH:mm, dd MMM yyyy",
                                    if (isEnglish) Locale.ENGLISH else Locale("id", "ID")
                                ).format(Date(time))
                            } ?: if (isEnglish) "Not updated yet" else "Belum diperbarui",
                            color = colors.onPrimary.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )

                        TextButton(
                            onClick = {
                                weatherViewModel.loadWeather(latitude, longitude)
                            },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colors.onPrimary
                            )
                        ) {
                            Text(if (isEnglish) "Refresh ↻" else "Perbarui ↻")
                        }
                    }
                }
            }
        }

        uiState.weather?.let { weather ->
            val current = weather.current
            val hourly = weather.hourly
            val daily = weather.daily

            item {
                WeatherSectionTitle(
                    title = if (isEnglish) "Weather Details" else "Detail Cuaca"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Humidity" else "Kelembapan",
                        value = "${current.humidity}%",
                        icon = "💧",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Speed" else "Kecepatan Angin",
                        value = "${current.windSpeed} km/h",
                        icon = "💨",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Direction" else "Arah Angin",
                        value = getWindDirection(current.windDirection, isEnglish),
                        icon = "🧭",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Gusts" else "Hembusan Angin",
                        value = String.format(
                            Locale.US,
                            "%.1f km/h",
                            current.windGusts
                        ),
                        icon = "🌬️",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                val uvLevel = when {
                    current.uvIndex < 3 -> if (isEnglish) "Low" else "Rendah"
                    current.uvIndex < 6 -> if (isEnglish) "Moderate" else "Sedang"
                    current.uvIndex < 8 -> if (isEnglish) "High" else "Tinggi"
                    current.uvIndex < 11 -> if (isEnglish) "Very High" else "Sangat Tinggi"
                    else -> if (isEnglish) "Extreme" else "Ekstrem"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "UV Index" else "Indeks UV",
                        value = "${String.format(Locale.US, "%.1f", current.uvIndex)} • $uvLevel",
                        icon = "☀️",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) "Pressure" else "Tekanan Udara",
                        value = "${current.pressureMsl.toInt()} hPa",
                        icon = "🌡️",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                val visibilityIndex = hourly.time.indexOfFirst {
                    it >= current.time
                }.let { if (it >= 0) it else 0 }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (visibilityIndex < hourly.visibility.size) {
                        WeatherInfoCard(
                            title = if (isEnglish) "Visibility" else "Jarak Pandang",
                            value = String.format(
                                Locale.US,
                                "%.1f km",
                                hourly.visibility[visibilityIndex] / 1000.0
                            ),
                            icon = "👁️",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (visibilityIndex < hourly.precipitationProbability.size) {
                        WeatherInfoCard(
                            title = if (isEnglish) "Rain Chance" else "Peluang Hujan",
                            value = "${hourly.precipitationProbability[visibilityIndex]}%",
                            icon = "🌧️",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                val rainIndex = hourly.time.indexOfFirst {
                    it >= current.time
                }.let { if (it >= 0) it else 0 }

                if (rainIndex < hourly.precipitation.size) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Precipitation" else "Curah Hujan",
                        value = String.format(
                            Locale.US,
                            "%.1f mm",
                            hourly.precipitation[rainIndex]
                        ),
                        icon = "🌧️",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                WeatherSectionTitle(
                    title = if (isEnglish) "Hourly Forecast" else "Prakiraan Per Jam",
                    subtitle = if (isEnglish) {
                        "Weather for the next hours"
                    } else {
                        "Perkiraan cuaca beberapa jam ke depan"
                    }
                )
            }

            item {
                val hourlyCount = minOf(
                    24,
                    hourly.time.size,
                    hourly.temperature.size,
                    hourly.weatherCode.size
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(hourly.time.take(hourlyCount)) { index, time ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(104.dp)
                                    .padding(vertical = 16.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = time.substringAfter("T"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                WeatherIcon(
                                    weatherCode = hourly.weatherCode[index],
                                    fontSize = 30.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "${hourly.temperature[index].toInt()}°",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (index < hourly.precipitationProbability.size) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "☔ ${hourly.precipitationProbability[index]}%",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (index < hourly.precipitation.size) {
                                    Text(
                                        text = String.format(
                                            Locale.US,
                                            "%.1f mm",
                                            hourly.precipitation[index]
                                        ),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                WeatherSectionTitle(
                    title = if (isEnglish) "Temperature Trend" else "Grafik Suhu"
                )
            }

            item {
                val chartCount = minOf(
                    24,
                    hourly.time.size,
                    hourly.temperature.size
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    TemperatureChart(
                        temperatures = hourly.temperature.take(chartCount),
                        times = hourly.time.take(chartCount),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                WeatherSectionTitle(
                    title = if (isEnglish) "7-Day Forecast" else "Prakiraan 7 Hari",
                    subtitle = if (isEnglish) {
                        "Weather for the coming week"
                    } else {
                        "Perkiraan cuaca selama seminggu"
                    }
                )
            }

            val dailyCount = minOf(
                7,
                daily.time.size,
                daily.weatherCode.size,
                daily.temperatureMax.size,
                daily.temperatureMin.size,
                daily.sunrise.size,
                daily.sunset.size
            )

            itemsIndexed(daily.time.take(dailyCount)) { index, date ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = formatDate(date, isEnglish),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )

                            Text(
                                text = getWeatherDescription(
                                    daily.weatherCode[index],
                                    isEnglish
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "🌅 ${daily.sunrise[index].substringAfter("T")}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "🌇 ${daily.sunset[index].substringAfter("T")}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        WeatherIcon(
                            weatherCode = daily.weatherCode[index],
                            fontSize = 32.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${daily.temperatureMax[index].toInt()}°",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "${daily.temperatureMin[index].toInt()}°",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1Weather",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun HeroWeatherDetail(
    icon: String,
    label: String,
    value: String
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 75.dp)
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.onPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeatherSectionTitle(
    title: String,
    subtitle: String? = null
) {
    val colors = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedWeatherIcon(
    weatherCode: Int,
    isDay: Boolean = true,
    fontSize: TextUnit
) {
    val isRainy = weatherCode in 51..67 ||
            weatherCode in 80..82 ||
            weatherCode in 95..99

    val isCloudy = weatherCode in 1..3 ||
            weatherCode == 45 ||
            weatherCode == 48

    val infiniteTransition = rememberInfiniteTransition(
        label = "weather_animation"
    )

    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "weather_scale"
    )

    val animatedOffsetX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "weather_horizontal"
    )

    val animatedOffsetY by infiniteTransition.animateFloat(
        initialValue = if (isRainy) 0f else -5f,
        targetValue = if (isRainy) 8f else 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isRainy) 700 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "weather_vertical"
    )

    val animatedModifier = when {
        isRainy -> Modifier.graphicsLayer {
            translationY = animatedOffsetY
        }

        isCloudy -> Modifier.graphicsLayer {
            translationX = animatedOffsetX
        }

        else -> Modifier.graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
            translationY = animatedOffsetY
        }
    }

    Box(
        modifier = Modifier
            .width(112.dp)
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .then(animatedModifier),
            contentAlignment = Alignment.Center
        ) {
            WeatherIcon(
                weatherCode = weatherCode,
                isDay = isDay,
                fontSize = fontSize
            )
        }
    }
}

@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: String = "🌤️"
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 17.sp
                )

                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }
    }
}

fun getWindDirection(
    degrees: Int,
    isEnglish: Boolean = false
): String {
    val directions = if (isEnglish) {
        listOf(
            "North",
            "Northeast",
            "East",
            "Southeast",
            "South",
            "Southwest",
            "West",
            "Northwest"
        )
    } else {
        listOf(
            "Utara",
            "Timur Laut",
            "Timur",
            "Tenggara",
            "Selatan",
            "Barat Daya",
            "Barat",
            "Barat Laut"
        )
    }

    val normalizedDegrees = ((degrees % 360) + 360) % 360
    val directionIndex = ((normalizedDegrees + 22) / 45) % 8

    return directions[directionIndex]
}

fun getWeatherDescription(
    code: Int,
    isEnglish: Boolean = false
): String {
    return when (code) {
        0 -> if (isEnglish) "Clear sky" else "Cerah"
        1 -> if (isEnglish) "Mainly clear" else "Cerah berawan"
        2 -> if (isEnglish) "Partly cloudy" else "Berawan sebagian"
        3 -> if (isEnglish) "Overcast" else "Mendung"
        45, 48 -> if (isEnglish) "Fog" else "Berkabut"
        in 51..57 -> if (isEnglish) "Drizzle" else "Gerimis"
        in 61..67 -> if (isEnglish) "Rain" else "Hujan"
        in 71..77 -> if (isEnglish) "Snow" else "Salju"
        in 80..82 -> if (isEnglish) "Rain showers" else "Hujan deras"
        in 85..86 -> if (isEnglish) "Snow showers" else "Hujan salju"
        in 95..99 -> if (isEnglish) "Thunderstorm" else "Badai petir"
        else -> if (isEnglish) "Unknown" else "Tidak diketahui"
    }
}

fun formatDate(
    date: String,
    isEnglish: Boolean = false
): String {
    return try {
        val locale = if (isEnglish) {
            Locale.ENGLISH
        } else {
            Locale.forLanguageTag("id-ID")
        }

        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val outputFormat = SimpleDateFormat(
            "EEEE, dd MMM",
            locale
        )

        val parsedDate = inputFormat.parse(date)

        if (parsedDate != null) {
            outputFormat.format(parsedDate)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}