package com.example.weatherapp.domain.model

class DayForecast(
    val readableDate: String? = "",
    val hourlyForecastList: List<ForecastElement>
)