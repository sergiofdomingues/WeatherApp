package com.example.weatherapp.data

import com.example.weatherapp.domain.Resource
import com.example.weatherapp.domain.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherConditionsDataSource {
    suspend fun get(city: String): Flow<Resource<WeatherForecast>>
}