package com.example.weatherapp.factories

import com.example.weatherapp.domain.ForecastElement

object ForecastElementFactory {
    fun build() =
        ForecastElement(
            iconUrl = "http://openweathermap.org/img/w/01d.png",
            temperature = 19,
            dateTimeInfo = DateTimeInfoFactory.build()
        )
}