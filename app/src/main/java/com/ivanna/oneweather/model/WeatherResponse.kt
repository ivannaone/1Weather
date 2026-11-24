package com.ivanna.oneweather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,

    @SerializedName("current")
    val current: CurrentWeather,

    @SerializedName("hourly")
    val hourly: HourlyWeather,

    @SerializedName("daily")
    val daily: DailyWeather
)

data class CurrentWeather(
    val time: String,

    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("relative_humidity_2m")
    val humidity: Int,

    @SerializedName("apparent_temperature")
    val feelsLike: Double,

    @SerializedName("is_day")
    val isDay: Int,

    @SerializedName("weather_code")
    val weatherCode: Int,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double,

    @SerializedName("uv_index")
    val uvIndex: Double,

    @SerializedName("precipitation")
    val precipitation: Double,

    @SerializedName("pressure_msl")
    val pressureMsl: Double,

    @SerializedName("wind_direction_10m")
    val windDirection: Int,

    @SerializedName("wind_gusts_10m")
    val windGusts: Double
)

data class HourlyWeather(
    val time: List<String>,

    @SerializedName("temperature_2m")
    val temperature: List<Double>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("precipitation_probability")
    val precipitationProbability: List<Int>,

    @SerializedName("precipitation")
    val precipitation: List<Double>,

    @SerializedName("visibility")
    val visibility: List<Double>
)

data class DailyWeather(
    val time: List<String>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,

    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,

    val sunrise: List<String>,
    val sunset: List<String>
)