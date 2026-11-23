package com.ivanna.oneweather.repository

import com.ivanna.oneweather.data.remote.ReverseGeocodingClient

class ReverseGeocodingRepository {

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Result<String> {
        return try {
            val response = ReverseGeocodingClient.api.getCityName(
                latitude,
                longitude
            )

            val address = response.address

            val city = address?.city
                ?: address?.town
                ?: address?.village
                ?: address?.county
                ?: address?.state

            if (city.isNullOrBlank()) {
                Result.failure(Exception("Nama kota tidak ditemukan"))
            } else {
                Result.success(city)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}