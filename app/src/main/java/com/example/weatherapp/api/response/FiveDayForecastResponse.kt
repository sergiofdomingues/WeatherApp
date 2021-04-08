package com.example.weatherapp.api.response

import com.google.gson.annotations.SerializedName

class FiveDayForecastResponse {
    @SerializedName("list")
    val forecastWeatherElements: List<WeatherResponse>? = null
}