package com.example.weatherapp.data.source

import com.example.weatherapp.data.response.FiveDayForecastResponse
import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastElement
import com.example.weatherapp.utils.Operation
import com.example.weatherapp.utils.wrapToOperation
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val weatherForecastService: WeatherForecastService) {

    suspend fun getCurrentWeather(cityName: String): Operation<ForecastElement> =
        weatherForecastService.getCurrentWeather(cityName)
            .toModel()
            .wrapToOperation()

    suspend fun getFiveDayForecast(cityName: String): Operation<List<DayForecast>> =
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