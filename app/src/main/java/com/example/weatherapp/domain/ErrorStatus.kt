package com.example.weatherapp.domain

class ErrorStatus(
    val errorType: ErrorType,
    val message: String? = null
) {
    enum class ErrorType {
        NowCallError,
        FiveDayCallError,
        UnavailableNowWeather,
        UnavailableFiveDayForecast,
        UnknownError
    }
}