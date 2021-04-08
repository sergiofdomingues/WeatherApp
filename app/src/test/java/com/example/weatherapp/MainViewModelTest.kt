package com.example.weatherapp

import com.example.weatherapp.factories.DayForecastFactory
import com.example.weatherapp.factories.ForecastElementFactory
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.usecase.GetWeatherForecast
import com.example.weatherapp.util.Operation
import com.example.weatherapp.viewmodel.MainViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Test

class MainViewModelTest {

    private val getWeatherForecast = mock<GetWeatherForecast>()

    @Test
    fun getCurrentWeatherTest() {
        setFiveDayForecastMockedDefaultBehavior()

        val currentWeather = ForecastElementFactory.build()
        whenever(getWeatherForecast.currentWeather(any())).thenReturn(
            Single.just(Operation.Success(currentWeather))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.currentWeather().test().assertValue(currentWeather)
    }

    @Test
    fun getTodayForecastChartDataTest() {
        setCurrentWeatherMockedDefaultBehavior()

        val fiveDayForecast = listOf(DayForecastFactory.build(), DayForecastFactory.build())
        whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
            Single.just(Operation.Success(fiveDayForecast))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.todayForecastChartData().test()
            .assertValue(mapToChartData(fiveDayForecast.first()))
    }

    @Test
    fun getFiveDayForecastTest() {
        setCurrentWeatherMockedDefaultBehavior()

        val fiveDayForecast = listOf(DayForecastFactory.build(), DayForecastFactory.build())
        whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
            Single.just(Operation.Success(fiveDayForecast))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.fiveDayForecast().test().assertValue(fiveDayForecast.drop(1))
    }

    @Test
    fun errorInCurrentWeatherStreamTest() {
        setFiveDayForecastMockedDefaultBehavior()

        whenever(getWeatherForecast.currentWeather(any())).thenReturn(
            Single.just(Operation.Error(Exception()))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.errors().test().run {
            assertValueCount(1)
            assertValue { it.errorName == MainViewModel.ErrorName.NetworkError }
        }
    }

    @Test
    fun errorInFiveDayForecastStreamTest() {
        setCurrentWeatherMockedDefaultBehavior()
        whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
            Single.just(Operation.Error(Exception()))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.errors().test().run {
            assertValueCount(1)
            assertValue { it.errorName == MainViewModel.ErrorName.NetworkError }
        }
    }

    @Test
    fun noForecastDataError() {
        setCurrentWeatherMockedDefaultBehavior()
        whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
            Single.just(Operation.Success(emptyList()))
        )

        val viewModel = buildViewModel()
        viewModel.init()
        viewModel.errors().test().run {
            assertValueCount(1)
            assertValue { it.errorName == MainViewModel.ErrorName.NoDataAvailable }
        }
    }


    // Helpers

    private fun buildViewModel() = MainViewModel(getWeatherForecast)

    private fun setCurrentWeatherMockedDefaultBehavior() {
        whenever(getWeatherForecast.currentWeather(any())).thenReturn(
            Single.just(Operation.Success(ForecastElementFactory.build()))
        )
    }

    private fun setFiveDayForecastMockedDefaultBehavior() {
        whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
            Single.just(
                Operation.Success(
                    listOf(DayForecastFactory.build(), DayForecastFactory.build())
                )
            )
        )
    }

    private fun mapToChartData(dayForecast: DayForecast) =
        dayForecast.hours.map {
            it.temperature.toString() to (it.dateTimeInfo?.hourStr ?: "")
        }.toMap()
}