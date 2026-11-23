package com.ivanna.oneweather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivanna.oneweather.data.remote.CityResult
import com.ivanna.oneweather.viewmodel.CitySearchViewModel

@Composable
fun SearchScreen(
    onCitySelected: (CityResult) -> Unit,
    selectedLanguage: String,
    searchViewModel: CitySearchViewModel = viewModel()
) {
    var cityName by rememberSaveable {
        mutableStateOf("")
    }

    val uiState by searchViewModel.uiState.collectAsState()
    val isEnglish = selectedLanguage == "English"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = if (isEnglish) "Search City" else "Cari Kota",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = cityName,
            onValueChange = { cityName = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    if (isEnglish) {
                        "Enter city name"
                    } else {
                        "Masukkan nama kota"
                    }
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                searchViewModel.searchCity(cityName)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = cityName.isNotBlank()
        ) {
            Text(
                if (isEnglish) "Search" else "Cari Kota"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: if (isEnglish) {
                        "An error occurred"
                    } else {
                        "Terjadi kesalahan"
                    },
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.cities.isEmpty() -> {
                Text(
                    text = if (isEnglish) {
                        "Search for a city to see the results"
                    } else {
                        "Cari kota untuk melihat hasilnya"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.cities) { city ->
                        CityItem(city = city) {
                            onCitySelected(city)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityItem(
    city: CityResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = listOfNotNull(
                    city.admin1,
                    city.country
                ).joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}