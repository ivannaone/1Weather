package com.ivanna.oneweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParseException
import com.ivanna.oneweather.model.WeatherResponse
import com.ivanna.oneweather.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val error: String? = null,
    val lastUpdated: Long? = null
)

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadWeather(latitude: Double, longitude: Double) {

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                if (
                    !latitude.isFinite() ||
                    !longitude.isFinite() ||
                    latitude !in -90.0..90.0 ||
                    longitude !in -180.0..180.0
                ) {
                    throw IllegalArgumentException(
                        "Lokasi tidak valid. Silakan pilih kota lain."
                    )
                }

                val result = repository.getWeather(
                    latitude,
                    longitude
                )

                result.onSuccess { weather ->

                    _uiState.value = WeatherUiState(
                        isLoading = false,
                        weather = weather,
                        error = null,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                result.onFailure { error ->

                    if (error is CancellationException) {
                        throw error
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = getErrorMessage(error)
                    )
                }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = getErrorMessage(error)
                )
            }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return when (error) {

            is UnknownHostException ->
                "Tidak ada koneksi internet. Periksa jaringan kamu."

            is SocketTimeoutException ->
                "Koneksi terlalu lama. Silakan coba lagi."

            is IOException ->
                "Terjadi masalah jaringan. Periksa koneksi internet."

            is HttpException -> {
                when (error.code()) {
                    400 ->
                        "Permintaan data tidak valid."

                    401, 403 ->
                        "Akses ke layanan cuaca ditolak."

                    404 ->
                        "Data cuaca tidak ditemukan."

                    429 ->
                        "Terlalu banyak permintaan. Coba beberapa saat lagi."

                    in 500..599 ->
                        "Server cuaca sedang bermasalah. Coba lagi nanti."

                    else ->
                        "Gagal mengambil data cuaca. Kode: ${error.code()}"
                }
            }

            is JsonParseException ->
                "Format data cuaca tidak valid."

            is IllegalArgumentException ->
                error.message ?: "Lokasi tidak valid."

            else ->
                "Terjadi kesalahan. Silakan coba lagi."
        }
    }
}