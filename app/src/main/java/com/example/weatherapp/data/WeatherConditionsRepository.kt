package com.example.weatherapp.data

import javax.inject.Inject

class WeatherConditionsRepository @Inject constructor(
    private val weatherConditionsDataSource: WeatherConditionsDataSource
) {
    suspend fun getWeatherConditions(city: String) = weatherConditionsDataSource.get(city)
}