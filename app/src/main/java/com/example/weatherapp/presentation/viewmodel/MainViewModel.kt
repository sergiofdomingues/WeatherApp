package com.example.weatherapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.model.WeatherForecast
import com.example.weatherapp.data.Repository
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _weatherForecast: MutableLiveData<Resource<WeatherForecast>> = MutableLiveData(null)

    init {
        fetchWeatherForecast()
    }

    private fun fetchWeatherForecast() {
        viewModelScope.launch {
            repository.getWeatherForecast(City)
                .catch { e -> _weatherForecast.postValue(Resource.Error.unknownError(e.toString())) }
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