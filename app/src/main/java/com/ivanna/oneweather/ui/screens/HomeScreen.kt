package com.ivanna.oneweather.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivanna.oneweather.viewmodel.WeatherViewModel
import com.ivanna.oneweather.ui.components.WeatherIcon
import com.ivanna.oneweather.ui.components.TemperatureChart
import com.ivanna.oneweather.ui.components.LocationWeatherButton
import com.ivanna.oneweather.repository.ReverseGeocodingRepository
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

    var currentLatitude by remember(latitude, longitude) {
        mutableStateOf(latitude)
    }

    var currentLongitude by remember(latitude, longitude) {
        mutableStateOf(longitude)
    }

    var displayedCityName by remember(cityName) {
        mutableStateOf(cityName)
    }

    val reverseGeocodingRepository = remember {
        ReverseGeocodingRepository()
    }

    LaunchedEffect(currentLatitude, currentLongitude) {
        weatherViewModel.loadWeather(
            currentLatitude,
            currentLongitude
        )
    }

    LaunchedEffect(currentLatitude, currentLongitude) {
        val result = reverseGeocodingRepository.getCityName(
            currentLatitude,
            currentLongitude
        )

        result.onSuccess { city ->
            displayedCityName = city
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {
            Column {
                Text(
                    text = "1Weather",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Text(
                    text = if (isEnglish) {
                        "Weather forecast"
                    } else {
                        "Prakiraan cuaca"
                    },
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onSearchClick) {
                        Text(
                            if (isEnglish) "Search City" else "Cari Kota"
                        )
                    }

                    TextButton(onClick = onSettingsClick) {
                        Text(
                            if (isEnglish) "Settings" else "Pengaturan"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LocationWeatherButton(
                    selectedLanguage = selectedLanguage,
                    onLocationFound = { lat, lon ->
                        currentLatitude = lat
                        currentLongitude = lon
                    }
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = displayedCityName.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        weatherViewModel.loadWeather(
                            currentLatitude,
                            currentLongitude
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isEnglish) {
                            "Refresh Weather"
                        } else {
                            "Perbarui Cuaca"
                        }
                    )
                }

                uiState.lastUpdated?.let { time ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isEnglish) {
                            "Last updated: ${
                                SimpleDateFormat(
                                    "HH:mm:ss, dd MMM yyyy",
                                    Locale.ENGLISH
                                ).format(Date(time))
                            }"
                        } else {
                            "Terakhir diperbarui: ${
                                SimpleDateFormat(
                                    "HH:mm:ss, dd MMM yyyy",
                                    Locale("id", "ID")
                                ).format(Date(time))
                            }"
                        },
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isLoading && uiState.weather == null) {
                    CircularProgressIndicator()

                    Text(
                        text = if (isEnglish) {
                            "Loading weather data..."
                        } else {
                            "Memuat data cuaca..."
                        },
                        color = colors.onSurface
                    )
                }

                if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 28.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isEnglish) {
                                    "Unable to update weather"
                                } else {
                                    "Gagal memperbarui cuaca"
                                },
                                fontWeight = FontWeight.Bold,
                                color = colors.onErrorContainer
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = uiState.error ?: "",
                                color = colors.onErrorContainer,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    weatherViewModel.loadWeather(
                                        currentLatitude,
                                        currentLongitude
                                    )
                                },
                                enabled = !uiState.isLoading
                            ) {
                                Text(
                                    if (isEnglish) "Try Again" else "Coba Lagi"
                                )
                            }
                        }
                    }
                }

                if (uiState.isLoading && uiState.weather != null) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                uiState.weather?.let { weather ->
                    val current = weather.current

                    AnimatedWeatherIcon(
                        weatherCode = current.weatherCode,
                        isDay = current.isDay == 1,
                        fontSize = 76.sp
                    )

                    Text(
                        text = "${current.temperature.toInt()}°",
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Light,
                        color = colors.onBackground
                    )

                    Text(
                        text = getWeatherDescription(
                            current.weatherCode,
                            isEnglish
                        ),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurface
                    )

                    Text(
                        text = if (isEnglish) {
                            "Feels like ${current.feelsLike.toInt()}°C"
                        } else {
                            "Terasa seperti ${current.feelsLike.toInt()}°C"
                        },
                        color = colors.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        uiState.weather?.let { weather ->
            val current = weather.current
            val hourly = weather.hourly
            val daily = weather.daily

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Humidity" else "Kelembapan",
                        value = "${current.humidity}%",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Speed" else "Kecepatan Angin",
                        value = "${current.windSpeed} km/h",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Direction" else "Arah Angin",
                        value = getWindDirection(
                            current.windDirection,
                            isEnglish
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) "Wind Gusts" else "Hembusan Angin",
                        value = String.format(
                            Locale.US,
                            "%.1f km/h",
                            current.windGusts
                        ),
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

                WeatherInfoCard(
                    title = if (isEnglish) "UV Index" else "Indeks UV",
                    value = "${String.format(Locale.US, "%.1f", current.uvIndex)} • $uvLevel",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                WeatherInfoCard(
                    title = if (isEnglish) "Atmospheric Pressure" else "Tekanan Udara",
                    value = "${current.pressureMsl.toInt()} hPa",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val visibilityIndex = hourly.time.indexOfFirst {
                    it >= current.time
                }.let {
                    if (it >= 0) it else 0
                }

                if (visibilityIndex < hourly.visibility.size &&
                    hourly.visibility.isNotEmpty()
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) "Visibility" else "Jarak Pandang",
                        value = String.format(
                            Locale.US,
                            "%.1f km",
                            hourly.visibility[visibilityIndex] / 1000.0
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                val rainIndex = hourly.time.indexOfFirst {
                    it >= current.time
                }.let {
                    if (it >= 0) it else 0
                }

                if (rainIndex < hourly.precipitationProbability.size &&
                    rainIndex < hourly.precipitation.size &&
                    hourly.precipitationProbability.isNotEmpty() &&
                    hourly.precipitation.isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WeatherInfoCard(
                            title = if (isEnglish) "Rain Probability" else "Peluang Hujan",
                            value = "${hourly.precipitationProbability[rainIndex]}%",
                            modifier = Modifier.weight(1f)
                        )

                        WeatherInfoCard(
                            title = if (isEnglish) "Precipitation" else "Curah Hujan",
                            value = String.format(
                                Locale.US,
                                "%.1f mm",
                                hourly.precipitation[rainIndex]
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = if (isEnglish) "Hourly Forecast" else "Prakiraan Per Jam",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
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
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colors.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = time.substringAfter("T"),
                                    color = colors.onSurfaceVariant,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                WeatherIcon(
                                    weatherCode = hourly.weatherCode[index],
                                    fontSize = 28.sp
                                )

                                Text(
                                    text = "${hourly.temperature[index].toInt()}°",
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = getWeatherDescription(
                                        hourly.weatherCode[index],
                                        isEnglish
                                    ),
                                    fontSize = 11.sp,
                                    color = colors.onSurfaceVariant
                                )

                                if (index < hourly.precipitationProbability.size) {
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "☔ ${hourly.precipitationProbability[index]}%",
                                        fontSize = 12.sp,
                                        color = colors.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (index < hourly.precipitation.size) {
                                    Text(
                                        text = String.format(
                                            Locale.US,
                                            "%.1f mm",
                                            hourly.precipitation[index]
                                        ),
                                        fontSize = 11.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                val chartCount = minOf(
                    24,
                    hourly.time.size,
                    hourly.temperature.size
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
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
                Text(
                    text = if (isEnglish) "7-Day Forecast" else "Prakiraan 7 Hari",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
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
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formatDate(date, isEnglish),
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface
                            )

                            Text(
                                text = getWeatherDescription(
                                    daily.weatherCode[index],
                                    isEnglish
                                ),
                                color = colors.onSurfaceVariant,
                                fontSize = 13.sp
                            )

                            Text(
                                text = "🌅 ${if (isEnglish) "Sunrise" else "Terbit"}: ${daily.sunrise[index].substringAfter("T")}",
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )

                            Text(
                                text = "🌇 ${if (isEnglish) "Sunset" else "Terbenam"}: ${daily.sunset[index].substringAfter("T")}",
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )
                        }

                        WeatherIcon(
                            weatherCode = daily.weatherCode[index],
                            fontSize = 30.sp
                        )

                        Text(
                            text = "${daily.temperatureMax[index].toInt()}° / ${daily.temperatureMin[index].toInt()}°",
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWeatherIcon(
    weatherCode: Int,
    isDay: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit
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
            .fillMaxWidth()
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

@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 18.sp,
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