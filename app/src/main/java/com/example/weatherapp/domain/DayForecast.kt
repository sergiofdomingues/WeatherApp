package com.example.weatherapp.domain

class DayForecast(
    val readableDate: String? = "",
    val hourlyForecastList: List<ForecastElement>
)