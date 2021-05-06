package com.example.weatherapp.domain

import com.example.weatherapp.framework.network.FiveDayForecastResponse
import com.example.weatherapp.domain.model.WeatherForecast
import com.example.weatherapp.framework.network.WeatherResponse
import com.example.weatherapp.utils.Resource.*
import kotlinx.coroutines.flow.*
import retrofit2.Response

abstract class NetworkBoundResource {

    suspend fun asFlow() = flow {
        emit(Loading(null))

        val data = loadFromDb()
        val flow = if (shouldFetch(data.first())) {
            emit(Loading(data.first()))

            val apiResponse = createCall()
            val now = apiResponse.first
            val fiveDay = apiResponse.second

            if (!now.isSuccessful) {
                data.map { Error.nowCallError(now.message(), it) }
            } else if (!fiveDay.isSuccessful) {
                data.map { Error.fiveDayCallError(fiveDay.message(), it) }
            } else {

                val nowResponse = now.body()
                val fiveDayResponse = fiveDay.body()

                when {
                    nowResponse == null -> data.map {
                        Error.unavailableNowWeatherData(it)
                    }
                    fiveDayResponse?.forecastWeatherElements == null -> data.map {
                        Error.unavailableFiveDayForecastData(it)
                    }
                    else -> {
                        saveCallResult(Pair(nowResponse, fiveDayResponse))
                        data.map { Success(it) }
                    }
                }
            }
        } else data.map { Success(it) } // Should warn user that the data is not fresh

        emitAll(flow)
    }

    abstract suspend fun createCall(): Pair<Response<WeatherResponse>, Response<FiveDayForecastResponse>>
    abstract suspend fun shouldFetch(data: WeatherForecast?): Boolean
    abstract suspend fun loadFromDb(): Flow<WeatherForecast>
    abstract suspend fun saveCallResult(items: Pair<WeatherResponse, FiveDayForecastResponse>)
}