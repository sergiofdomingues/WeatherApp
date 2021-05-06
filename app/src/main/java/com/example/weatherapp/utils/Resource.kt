package com.example.weatherapp.utils

import com.example.weatherapp.domain.model.ErrorStatus
import com.example.weatherapp.domain.model.ErrorStatus.ErrorType

sealed class Resource<out T>(
    val data: T? = null,
    val error: ErrorStatus? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(error: ErrorStatus, data: T? = null) : Resource<T>(data, error) {

        companion object {
            fun <T> nowCallError(msg: String, data: T? = null) =
                Error(ErrorStatus(ErrorType.NowCallError, msg), data)

            fun <T> fiveDayCallError(msg: String, data: T? = null) =
                Error(ErrorStatus(ErrorType.FiveDayCallError, msg), data)

            fun <T> unavailableNowWeatherData(data: T? = null) =
                Error(ErrorStatus(ErrorType.UnavailableNowWeather), data)

            fun <T> unavailableFiveDayForecastData(data: T? = null) =
                Error(ErrorStatus(ErrorType.UnavailableFiveDayForecast), data)

            fun unknownError(msg: String) = Error(ErrorStatus(ErrorType.UnknownError, msg), null)
        }
    }
}