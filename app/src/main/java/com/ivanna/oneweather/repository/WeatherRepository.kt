package com.ivanna.oneweather.repository

import com.ivanna.oneweather.data.remote.WeatherApiClient
import com.ivanna.oneweather.model.WeatherResponse
import kotlinx.coroutines.CancellationException

class WeatherRepository {

    private val api = WeatherApiClient.api

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse> {

        return try {
            val response = api.getWeather(
                latitude = latitude,
                longitude = longitude
            )

            Result.success(response)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}