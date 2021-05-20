package com.example.weatherapp.presentation.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.Resource
import com.example.weatherapp.domain.WeatherForecast
import com.example.weatherapp.usecases.GetWeather
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getWeather: GetWeather
) : ViewModel() {

    private val _weatherForecast: MutableLiveData<Resource<WeatherForecast>> = MutableLiveData(null)

    init {
        fetchWeatherForecast()
    }

    private fun fetchWeatherForecast() {
        viewModelScope.launch {
            getWeather(city = City)
                .catch { e ->
                    _weatherForecast.postValue(Resource.Error.unknownError(e.toString()))
                }
                .collect {
                    _weatherForecast.postValue(it)
                }
        }
    }

    // Input

    fun onRefresh() = fetchWeatherForecast()

    // Output

    val weatherForecast: LiveData<Resource<WeatherForecast>> = _weatherForecast

    companion object {
        private const val City = "Porto"
    }
}