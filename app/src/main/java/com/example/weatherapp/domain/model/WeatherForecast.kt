package com.example.weatherapp.domain.model

data class WeatherForecast(
    val currentWeather: ForecastElement? = null,
    val todayForecast: Map<String, String>? = null,
    val fiveDayForecast: List<DayForecast> = emptyList()
)