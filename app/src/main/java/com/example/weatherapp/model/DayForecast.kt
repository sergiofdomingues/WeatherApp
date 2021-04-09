package com.example.weatherapp.model

class DayForecast(
    val readableDate: String? = "",
    val hourlyForecastList: List<ForecastElement>
)