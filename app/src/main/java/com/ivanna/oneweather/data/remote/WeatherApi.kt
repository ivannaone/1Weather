package com.ivanna.oneweather.data.remote

import com.ivanna.oneweather.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String =
            "temperature_2m,weather_code",
        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

object WeatherApiClient {

    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}