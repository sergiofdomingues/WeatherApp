package com.example.weatherapp.factories

import com.example.weatherapp.domain.model.ForecastElement

object DateTimeInfoFactory {
    fun build() =
        ForecastElement.DateTimeInfo(
            dayStr = "2021-04-08",
            hourStr = "09",
            readableDate = "Thursday, April 8"
        )
}