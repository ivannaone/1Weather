package com.ivanna.oneweather.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ReverseGeocodingResponse(
    val address: Address?
)

data class Address(
    val city: String?,
    val town: String?,
    val village: String?,
    val county: String?,
    val state: String?
)

interface ReverseGeocodingApi {
    @GET("reverse")
    suspend fun getCityName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("zoom") zoom: Int = 10,
        @Query("addressdetails") addressDetails: Int = 1
    ): ReverseGeocodingResponse
}

object ReverseGeocodingClient {
    val api: ReverseGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReverseGeocodingApi::class.java)
    }
}