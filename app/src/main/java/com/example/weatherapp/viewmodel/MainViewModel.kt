package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.ForecastElement
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.rx.Operation
import com.example.weatherapp.usecase.GetWeatherForecast
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getWeatherForecast: GetWeatherForecast
) : ViewModel() {

    private val disposables by lazy { CompositeDisposable() }

    // Input
    private lateinit var refreshes: PublishRelay<Unit>

    // Intermediate
    private lateinit var getCurrentWeather: PublishRelay<Unit>
    private lateinit var getForecast: PublishRelay<Unit>
    private lateinit var todayForecast: BehaviorRelay<DayForecast>

    // Output
    private lateinit var currentWeather: BehaviorRelay<ForecastElement>
    private lateinit var todayForecastChartData: BehaviorRelay<Map<String, String>>
    private lateinit var fiveDayForecast: BehaviorRelay<List<DayForecast>>
    private lateinit var errors: BehaviorRelay<Error>
    private lateinit var isLoading: PublishRelay<Boolean>

    fun init() {
        resetStreams()

        getCurrentWeather
            .flatMapSingle { getWeatherForecast.currentWeather(City) }
            .subscribe {
                when (it) {
                    is Operation.Success<ForecastElement> -> {
                        currentWeather.accept(it.result)
                    }
                    is Operation.Error<ForecastElement> -> {
                        errors.accept(Error(ErrorName.NetworkError, it.throwable.message))
                    }
                }
            }
            .addTo(disposables)

        getForecast
            .flatMapSingle { getWeatherForecast.fiveDayForecast(City) }
            .subscribe {
                when (it) {
                    is Operation.Success<List<DayForecast>> -> {
                        if (it.result.isNotEmpty()) {
                            todayForecast.accept(it.result.first())
                            fiveDayForecast.accept(it.result.drop(1))
                        } else {
                            errors.accept(Error(ErrorName.NoDataAvailable))
                        }
                    }
                    is Operation.Error<List<DayForecast>> -> {
                        errors.accept(Error(ErrorName.NetworkError, it.throwable.message))
                    }
                }
                isLoading.accept(false)
            }
            .addTo(disposables)

        // Mapping to Map<Temperature, Hour>
        todayForecast
            .map { dayForecast ->
                dayForecast.hours.map {
                    it.temperature.toString() to (it.dateTimeInfo?.hourStr ?: "")
                }.toMap()
            }
            .subscribe {
                todayForecastChartData.accept(it)
            }
            .addTo(disposables)

        refreshes
            .doOnNext { isLoading.accept(true) }
            .subscribe {
                getCurrentWeather.accept(Unit)
                getForecast.accept(Unit)
            }
            .addTo(disposables)

        refreshes.accept(Unit)
    }

    private fun resetStreams() {
        currentWeather = BehaviorRelay.create()
        todayForecast = BehaviorRelay.create()
        fiveDayForecast = BehaviorRelay.create()
        todayForecastChartData = BehaviorRelay.create()
        getCurrentWeather = PublishRelay.create()
        getForecast = PublishRelay.create()
        refreshes = PublishRelay.create()
        isLoading = PublishRelay.create()
        errors = BehaviorRelay.create()
    }

    public override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    // Input

    fun onRefresh() = refreshes.accept(Unit)

    // Output

    fun currentWeather() = currentWeather.hide()
    fun fiveDayForecast() = fiveDayForecast.hide()
    fun todayForecastChartData() = todayForecastChartData.hide()
    fun errors() = errors.hide()
    fun isLoading() = isLoading.hide()

    // Helpers

    enum class ErrorName {
        NoDataAvailable,
        NetworkError
    }

    data class Error(
        val errorName: ErrorName,
        val message: String? = null
    )

    companion object {
        private const val City = "Porto"
    }
}