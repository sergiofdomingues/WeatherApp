package com.example.weatherapp.usecases

import com.example.weatherapp.data.WeatherConditionsRepository
import javax.inject.Inject

class GetWeather @Inject constructor(private val weatherConditionsRepository: WeatherConditionsRepository) {
    suspend operator fun invoke(city: String) =
        weatherConditionsRepository.getWeatherConditions(city)
}