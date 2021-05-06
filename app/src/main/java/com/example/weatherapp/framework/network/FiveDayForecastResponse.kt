package com.example.weatherapp.framework.network

import com.google.gson.annotations.SerializedName

class FiveDayForecastResponse(
    @SerializedName("list")
    val forecastWeatherElements: List<WeatherResponse>? = null
)