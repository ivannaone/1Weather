package com.ivanna.oneweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanna.oneweather.data.remote.CityResult
import com.ivanna.oneweather.repository.CityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CitySearchUiState(
    val isLoading: Boolean = false,
    val cities: List<CityResult> = emptyList(),
    val error: String? = null
)

class CitySearchViewModel : ViewModel() {

    private val repository = CityRepository()

    private val _uiState =
        MutableStateFlow(CitySearchUiState())

    val uiState: StateFlow<CitySearchUiState> =
        _uiState.asStateFlow()

    fun searchCity(cityName: String) {
        if (cityName.isBlank()) {
            _uiState.value = CitySearchUiState()
            return
        }

        viewModelScope.launch {
            _uiState.value = CitySearchUiState(
                isLoading = true
            )

            val result = repository.searchCity(cityName)

            result.onSuccess { cities ->
                _uiState.value = CitySearchUiState(
                    cities = cities,
                    error = if (cities.isEmpty()) {
                        "Kota tidak ditemukan"
                    } else {
                        null
                    }
                )
            }

            result.onFailure { exception ->
                _uiState.value = CitySearchUiState(
                    error = exception.message
                        ?: "Gagal mencari kota"
                )
            }
        }
    }
}