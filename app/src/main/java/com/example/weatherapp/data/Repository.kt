package com.example.weatherapp.data

import com.example.weatherapp.domain.viewmodel.MainViewModel
import com.example.weatherapp.domain.viewmodel.MainViewModel.Error
import com.example.weatherapp.domain.viewmodel.MainViewModel.ErrorName.NetworkError
import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastElement
import com.example.weatherapp.data.source.RemoteDataSource
import com.example.weatherapp.utils.Operation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    private val _nowWeatherConditions = MutableStateFlow<ForecastElement?>(null)
    private val _fiveDayWeatherConditions = MutableStateFlow<List<DayForecast>>(emptyList())
    private val _errors = MutableStateFlow<Error?>(null)

    val nowWeatherConditions: Flow<ForecastElement?> = _nowWeatherConditions
    val fiveDayWeatherConditions: Flow<List<DayForecast>> = _fiveDayWeatherConditions
    val errors: Flow<Error?> = _errors

    suspend fun getWeatherForecast(cityName: String) =
        withContext(Dispatchers.IO) {
            val current = async { remoteDataSource.getCurrentWeather(cityName) }
            val fiveDay = async { remoteDataSource.getFiveDayForecast(cityName) }
            castResults(current.await(), fiveDay.await())
        }

    private fun castResults(
        nowWeather: Operation<ForecastElement>,
        fiveDayWeather: Operation<List<DayForecast>>
    ) {
        when (nowWeather) {
            is Operation.Success<ForecastElement> -> _nowWeatherConditions.value =
                nowWeather.result
            is Operation.Error<ForecastElement> -> _errors.value =
                Error(NetworkError, nowWeather.throwable.message)
        }

        when (fiveDayWeather) {
            is Operation.Success<List<DayForecast>> -> {
                if (fiveDayWeather.result.isNotEmpty()) {
                    _fiveDayWeatherConditions.value = fiveDayWeather.result
                } else _errors.value = Error(MainViewModel.ErrorName.NoDataAvailable)
            }
            is Operation.Error<List<DayForecast>> -> _errors.value =
                Error(NetworkError, fiveDayWeather.throwable.message)
        }
    }
}