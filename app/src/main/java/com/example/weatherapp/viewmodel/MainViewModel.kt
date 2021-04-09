package com.example.weatherapp.viewmodel

import androidx.lifecycle.*
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.model.ForecastElement
import com.example.weatherapp.usecase.GetWeatherForecast
import com.example.weatherapp.util.Operation
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getWeatherForecast: GetWeatherForecast
) : ViewModel() {

    // Intermediate
    private val currentWeather: MutableLiveData<ForecastElement> = MutableLiveData(null)
    private val todayForecastChartData: MutableLiveData<Map<String, String>> = MutableLiveData(null)
    private val fiveDayForecast: MutableLiveData<List<DayForecast>> = MutableLiveData(null)

    // Output data
    private val errors: MutableLiveData<Error?> = MutableLiveData(null)
    private val isLoading: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val combinedForecastData: LiveData<ForecastData> =
        zipLiveData(currentWeather, todayForecastChartData, fiveDayForecast)

    private suspend fun fetchData() {
        isLoading.postValue(true)
        castCurrentWeatherResponse(getWeatherForecast.currentWeather(City))
        castFiveDayForecastResponse(getWeatherForecast.fiveDayForecast(City))
    }

    private fun castCurrentWeatherResponse(response: Operation<ForecastElement>) {
        when (response) {
            is Operation.Success<ForecastElement> -> currentWeather.postValue(response.result)
            is Operation.Error<ForecastElement> -> errors.postValue(
                Error(ErrorName.NetworkError, response.throwable.message)
            )
        }
    }

    private fun castFiveDayForecastResponse(response: Operation<List<DayForecast>>) {
        when (response) {
            is Operation.Success<List<DayForecast>> -> {
                if (response.result.isNotEmpty()) {
                    todayForecastChartData.postValue(response.result.first().mapToChartData())
                    fiveDayForecast.postValue(response.result.drop(1))
                } else errors.postValue(Error(ErrorName.NoDataAvailable))
            }
            is Operation.Error<List<DayForecast>> -> errors.postValue(
                Error(ErrorName.NetworkError, response.throwable.message)
            )
        }
    }

    // Input

    fun onRefresh() = viewModelScope.launch { fetchData() }

    // Output

    fun combinedForecastData(): LiveData<ForecastData> = combinedForecastData
    fun isLoading(): LiveData<Boolean?> = isLoading
    fun errors(): LiveData<Error?> = errors

    // Helpers

    // Mapping to Map<Temperature, Hour>
    private fun DayForecast.mapToChartData() = hourlyForecastList.map {
        it.temperature.toString() to (it.dateTimeInfo?.hourStr ?: "")
    }.toMap()

    private fun zipLiveData(
        currentWeather: LiveData<ForecastElement>,
        todayForecastChartData: LiveData<Map<String, String>>,
        fiveDayForecast: LiveData<List<DayForecast>>
    ): LiveData<ForecastData> {
        return MediatorLiveData<ForecastData>().apply {
            var lastCurrentWeather: ForecastElement? = null
            var lastTodayForecast: Map<String, String>? = null
            var lastFiveDayForecast: List<DayForecast>? = null

            fun update() {
                lastCurrentWeather?.let { current ->
                    lastTodayForecast?.let { today ->
                        lastFiveDayForecast?.let { fiveDay ->
                            this.value = ForecastData(current, today, fiveDay)
                            isLoading.postValue(false)
                        }
                    }
                }
            }

            addSource(currentWeather) {
                lastCurrentWeather = it
                update()
            }
            addSource(todayForecastChartData) {
                lastTodayForecast = it
                update()
            }
            addSource(fiveDayForecast) {
                lastFiveDayForecast = it
                update()
            }
        }
    }

    enum class ErrorName {
        NoDataAvailable,
        NetworkError
    }

    data class Error(
        val errorName: ErrorName,
        val message: String? = null
    )

    data class ForecastData(
        val currentWeather: ForecastElement,
        val todayForecast: Map<String, String>,
        val fiveDayForecast: List<DayForecast>
    )

    companion object {
        private const val City = "Porto"
    }
}