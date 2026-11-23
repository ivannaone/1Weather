package com.ivanna.oneweather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // Menyimpan nama kota yang sedang ditampilkan
    var displayedCityName by remember(cityName) {
        mutableStateOf(cityName)
    }

    // Repository untuk mendapatkan nama kota dari koordinat
    val reverseGeocodingRepository = remember {
        ReverseGeocodingRepository()
    }

    // Mengambil data cuaca berdasarkan koordinat
    LaunchedEffect(currentLatitude, currentLongitude) {
        weatherViewModel.loadWeather(
            currentLatitude,
            currentLongitude
        )
    }

    // Mengambil nama kota berdasarkan koordinat
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

                // Tombol memperbarui data cuaca
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

                // Waktu terakhir data cuaca berhasil diperbarui
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

                when {
                    uiState.isLoading -> {
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

                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: if (isEnglish) {
                                "An error occurred"
                            } else {
                                "Terjadi kesalahan"
                            },
                            color = colors.error
                        )

                        Button(
                            onClick = {
                                weatherViewModel.loadWeather(
                                    currentLatitude,
                                    currentLongitude
                                )
                            }
                        ) {
                            Text(
                                if (isEnglish) "Try Again" else "Coba Lagi"
                            )
                        }
                    }

                    uiState.weather != null -> {
                        val current = uiState.weather!!.current

                        WeatherIcon(
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
        }

        if (uiState.weather != null) {
            val current = uiState.weather!!.current

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WeatherInfoCard(
                        title = if (isEnglish) {
                            "Humidity"
                        } else {
                            "Kelembapan"
                        },
                        value = "${current.humidity}%",
                        modifier = Modifier.weight(1f)
                    )

                    WeatherInfoCard(
                        title = if (isEnglish) {
                            "Wind Speed"
                        } else {
                            "Kecepatan Angin"
                        },
                        value = "${current.windSpeed} km/h",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = if (isEnglish) {
                        "Hourly Forecast"
                    } else {
                        "Prakiraan Per Jam"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            item {
                val hourly = uiState.weather!!.hourly

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
                            }
                        }
                    }
                }
            }

            // Grafik perubahan suhu
            item {
                val hourly = uiState.weather!!.hourly

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
                    text = if (isEnglish) {
                        "7-Day Forecast"
                    } else {
                        "Prakiraan 7 Hari"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            itemsIndexed(uiState.weather!!.daily.time) { index, date ->
                val daily = uiState.weather!!.daily

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
                                text = "🌅 ${
                                    if (isEnglish) "Sunrise" else "Terbit"
                                }: ${daily.sunrise[index].substringAfter("T")}",
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )

                            Text(
                                text = "🌇 ${
                                    if (isEnglish) "Sunset" else "Terbenam"
                                }: ${daily.sunset[index].substringAfter("T")}",
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