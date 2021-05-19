package com.example.weatherapp.factories

import com.example.weatherapp.domain.DayForecast

object DayForecastFactory {
    fun build() =
        DayForecast(
            readableDate = "Thursday, April 8",
            hourlyForecastList = listOf(ForecastElementFactory.build())
        )
}