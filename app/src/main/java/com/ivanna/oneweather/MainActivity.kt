package com.ivanna.oneweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.ivanna.oneweather.data.remote.CityResult
import com.ivanna.oneweather.ui.screens.HomeScreen
import com.ivanna.oneweather.ui.screens.SearchScreen
import com.ivanna.oneweather.ui.screens.SettingsScreen
import com.ivanna.oneweather.ui.theme._1WeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val preferences = getSharedPreferences("one_weather_prefs", MODE_PRIVATE)
        val hasSeenOnboarding = preferences.getBoolean("has_seen_onboarding", false)

        setContent {
            var isDarkMode by rememberSaveable {
                mutableStateOf(false)
            }

            var selectedLanguage by rememberSaveable {
                mutableStateOf("Indonesia")
            }

            var selectedCity by rememberSaveable {
                mutableStateOf("Surakarta")
            }

            var latitude by rememberSaveable {
                mutableStateOf(-7.5755)
            }

            var longitude by rememberSaveable {
                mutableStateOf(110.8243)
            }

            var showSearch by rememberSaveable {
                mutableStateOf(false)
            }

            var showSettings by rememberSaveable {
                mutableStateOf(false)
            }

            var showSplash by rememberSaveable {
                mutableStateOf(true)
            }

            var showOnboarding by rememberSaveable {
                mutableStateOf(!hasSeenOnboarding)
            }

            LaunchedEffect(Unit) {
                delay(1500)
                showSplash = false
            }

            _1WeatherTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        showSplash -> {
                            SplashScreen()
                        }

                        showOnboarding -> {
                            OnboardingScreen(
                                onStartClick = {
                                    preferences.edit()
                                        .putBoolean("has_seen_onboarding", true)
                                        .apply()

                                    showOnboarding = false
                                }
                            )
                        }

                        showSettings -> {
                            SettingsScreen(
                                isDarkMode = isDarkMode,
                                onDarkModeChange = {
                                    isDarkMode = it
                                },
                                selectedLanguage = selectedLanguage,
                                onLanguageChange = {
                                    selectedLanguage = it
                                },
                                onBackClick = {
                                    showSettings = false
                                }
                            )
                        }

                        showSearch -> {
                            SearchScreen(
                                selectedLanguage = selectedLanguage,
                                onCitySelected = { city: CityResult ->
                                    selectedCity = city.name
                                    latitude = city.latitude
                                    longitude = city.longitude
                                    showSearch = false
                                }
                            )
                        }

                        else -> {
                            HomeScreen(
                                cityName = selectedCity,
                                latitude = latitude,
                                longitude = longitude,
                                selectedLanguage = selectedLanguage,
                                onSearchClick = {
                                    showSearch = true
                                },
                                onSettingsClick = {
                                    showSettings = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF0D47A1)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "☀️",
                fontSize = 76.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "1Weather",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your daily weather companion",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "1Weather",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val secondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(
                    color = primaryColor.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌤️",
                fontSize = 76.sp
            )
        }

        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = "Selamat Datang di 1Weather",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Pantau kondisi cuaca dengan mudah. Dapatkan informasi suhu, kelembapan, kecepatan angin, prakiraan harian, dan informasi cuaca lainnya.",
            fontSize = 15.sp,
            lineHeight = 24.sp,
            color = secondaryColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        OnboardingFeature(
            emoji = "🌡️",
            title = "Informasi Cuaca",
            description = "Lihat kondisi cuaca dan suhu terkini."
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnboardingFeature(
            emoji = "📍",
            title = "Pencarian Lokasi",
            description = "Cari informasi cuaca berbagai kota."
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnboardingFeature(
            emoji = "📅",
            title = "Prakiraan Cuaca",
            description = "Pantau prakiraan cuaca hingga 7 hari."
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Mulai Sekarang",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun OnboardingFeature(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 23.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}