package com.example.weatherapp.usecase

import com.example.weatherapp.api.WeatherForecastService
import com.example.weatherapp.api.response.FiveDayForecastResponse
import com.example.weatherapp.model.ForecastElement
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.rx.toOperation
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GetWeatherForecast @Inject constructor(private val weatherForecastService: WeatherForecastService) {

    fun currentWeather(cityName: String) = weatherForecastService.getCurrentWeather(cityName)
        .subscribeOn(Schedulers.io())
        .map { it.toModel() }
        .toOperation()

    fun fiveDayForecast(cityName: String) = weatherForecastService.getFiveDayForecast(cityName)
        .subscribeOn(Schedulers.io())
        .map { it.toListOfDays() }
        .toOperation()

    private fun FiveDayForecastResponse.toListOfDays(): List<DayForecast> {

        // Mapping response elements to model
        val modelList: List<ForecastElement> = forecastWeatherElements?.let {
            it.map { weatherResponse -> weatherResponse.toModel() }
        } ?: emptyList()

        // Grouping forecast elements by day
        return modelList.groupBy { weatherData -> weatherData.dateTimeInfo?.dayStr }.entries.map {
            DayForecast(
                readableDate = it.value.first().dateTimeInfo?.readableDate ?: "",
                hours = it.value
            )
        }
    }
}