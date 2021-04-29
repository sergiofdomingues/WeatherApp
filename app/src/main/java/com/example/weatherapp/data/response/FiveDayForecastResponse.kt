package com.example.weatherapp.data.response

import com.google.gson.annotations.SerializedName

class FiveDayForecastResponse {
    @SerializedName("list")
    val forecastWeatherElements: List<WeatherResponse>? = null
}