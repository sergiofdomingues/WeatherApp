package com.example.weatherapp.data

import androidx.room.withTransaction
import com.example.weatherapp.domain.model.WeatherForecast
import com.example.weatherapp.framework.network.WeatherForecastService
import com.example.weatherapp.framework.network.FiveDayForecastResponse
import com.example.weatherapp.domain.NetworkBoundResource
import com.example.weatherapp.framework.database.*
import com.example.weatherapp.framework.network.WeatherResponse
import com.example.weatherapp.utils.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val db: WeatherDatabase,
    private val weatherDao: WeatherDao,
    private val weatherForecastService: WeatherForecastService,
    private val connectionManager: ConnectionManager
) {
    suspend fun getWeatherForecast(cityName: String) =
        withContext(Dispatchers.IO) {
            object : NetworkBoundResource() {

                override suspend fun createCall(): Pair<Response<WeatherResponse>, Response<FiveDayForecastResponse>> =
                    coroutineScope {
                        val current = async { weatherForecastService.getCurrentWeather(cityName) }
                        val fiveDay = async { weatherForecastService.getFiveDayForecast(cityName) }
                        Pair(current.await(), fiveDay.await())
                    }

                override suspend fun shouldFetch(data: WeatherForecast?) =
                    connectionManager.isNetworkAvailable()

                override suspend fun loadFromDb(): Flow<WeatherForecast> = flow {

                    val now: NowWeatherDbo? = weatherDao.getNowWeatherConditions().first()
                    now?.let {
                        it.nowId?.let { id ->
                            val fiveDayList = weatherDao.getFiveDayWeatherElements(id)
                            emit(asUiData(it, fiveDayList.first() ?: emptyList()))
                        }
                    }
                }

                override suspend fun saveCallResult(items: Pair<WeatherResponse, FiveDayForecastResponse>) {
                    val nowWeatherDbo = asNowWeatherDBModel(items.first)
                    db.withTransaction {
                        weatherDao.clearAllTables()
                        val nowRowId = weatherDao.insertWeather(nowWeatherDbo)
                        weatherDao.insertFiveDayElements(
                            asFiveDayWeatherElementsDBModel(nowRowId, items.second)
                        )
                    }
                }
            }.asFlow()
        }
}