package com.example.weatherapp.domain.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastElement
import com.example.weatherapp.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _weatherConditions: Flow<ForecastData>
        get() = repository.nowWeatherConditions
            .combine(repository.fiveDayWeatherConditions) { now, fiveDay ->
                mergeResponses(now, fiveDay)
            }
            .onEach { _isLoading.value = false }
            .flowOn(Dispatchers.Default)
            .conflate()

    init {
        fetchWeatherForecast()
    }

    private fun fetchWeatherForecast() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getWeatherForecast(cityName = City)
        }
    }

    // Input

    fun onRefresh() = fetchWeatherForecast()

    // Output

    val weatherConditions: LiveData<ForecastData?> = _weatherConditions.asLiveData()
    val isLoading: LiveData<Boolean> = _isLoading.asLiveData()
    val errors: LiveData<Error?> = repository.errors.asLiveData()

    // Helpers

    private suspend fun mergeResponses(
        nowWeatherConditions: ForecastElement?,
        fiveDayWeatherConditions: List<DayForecast>?
    ) = withContext(Dispatchers.Default) {
        ForecastData(
            currentWeather = nowWeatherConditions,
            todayForecast = fiveDayWeatherConditions?.firstOrNull()?.mapToChartData(),
            fiveDayForecast = fiveDayWeatherConditions?.drop(1) ?: emptyList()
        )
    }

    // Mapping to Map<Temperature, Hour>
    private fun DayForecast.mapToChartData() = hourlyForecastList.map {
        it.temperature.toString() to (it.dateTimeInfo?.hourStr ?: "")
    }.toMap()

    enum class ErrorName {
        NoDataAvailable,
        NetworkError
    }

    data class Error(
        val errorName: ErrorName,
        val message: String? = null
    )

    data class ForecastData(
        val currentWeather: ForecastElement? = null,
        val todayForecast: Map<String, String>? = null,
        val fiveDayForecast: List<DayForecast> = emptyList()
    )

    companion object {
        private const val City = "Porto"
    }
}