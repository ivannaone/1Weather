package com.ivanna.oneweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.ivanna.oneweather.data.remote.CityResult
import com.ivanna.oneweather.ui.screens.HomeScreen
import com.ivanna.oneweather.ui.screens.SearchScreen
import com.ivanna.oneweather.ui.screens.SettingsScreen
import com.ivanna.oneweather.ui.theme._1WeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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

            _1WeatherTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
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