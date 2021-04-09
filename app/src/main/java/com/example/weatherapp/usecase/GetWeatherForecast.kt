package com.example.weatherapp.usecase

import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.api.response.FiveDayForecastResponse
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.model.ForecastElement
import com.example.weatherapp.util.wrapToOperation
import javax.inject.Inject

class GetWeatherForecast @Inject constructor(private val weatherForecastService: WeatherForecastService) {

    suspend fun currentWeather(cityName: String) =
        weatherForecastService.getCurrentWeather(cityName)
            .toModel()
            .wrapToOperation()

    suspend fun fiveDayForecast(cityName: String) =
        weatherForecastService.getFiveDayForecast(cityName)
            .toListOfDays()
            .wrapToOperation()

    private fun FiveDayForecastResponse.toListOfDays(): List<DayForecast> {

        // Mapping response elements to model
        val modelList: List<ForecastElement> = forecastWeatherElements?.let {
            it.map { weatherResponse -> weatherResponse.toModel() }
        } ?: emptyList()

        // Grouping forecast elements by day
        return modelList.groupBy { weatherData -> weatherData.dateTimeInfo?.dayStr }.entries.map {
            DayForecast(
                readableDate = it.value.first().dateTimeInfo?.readableDate ?: "",
                hourlyForecastList = it.value
            )
        }
    }
}