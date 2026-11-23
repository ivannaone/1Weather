package com.ivanna.oneweather.repository

import com.ivanna.oneweather.data.remote.GeocodingApiClient
import com.ivanna.oneweather.data.remote.CityResult

class CityRepository {

    private val api = GeocodingApiClient.api

    suspend fun searchCity(
        cityName: String
    ): Result<List<CityResult>> {

        return try {
            val response = api.searchCity(
                cityName = cityName
            )

            Result.success(
                response.results ?: emptyList()
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}