package com.example.weatherapp.domain.model

import java.util.*

data class ForecastElement(
    val iconUrl: String,
    val temperature: Int,
    val dateTimeInfo: DateTimeInfo? = null
) {

    data class DateTimeInfo(
        val dayStr: String,
        val hourStr: String,
        val readableDate: String,
        val date: Date? = null
    )
}