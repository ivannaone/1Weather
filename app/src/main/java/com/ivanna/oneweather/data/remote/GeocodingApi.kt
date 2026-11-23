package com.ivanna.oneweather.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResponse(
    val results: List<CityResult>?
)

data class CityResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)

interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "id",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}

object GeocodingApiClient {

    private const val BASE_URL =
        "https://geocoding-api.open-meteo.com/"

    val api: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(GeocodingApi::class.java)
    }
}