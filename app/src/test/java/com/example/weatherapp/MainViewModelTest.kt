package com.example.weatherapp

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherapp.factories.DayForecastFactory
import com.example.weatherapp.factories.ForecastElementFactory
import com.example.weatherapp.usecase.GetWeatherForecast
import com.example.weatherapp.util.Operation
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    private val getWeatherForecast = mock<GetWeatherForecast>()
    private lateinit var viewModel: MainViewModel
    private val forecastDataObserver = mock<Observer<MainViewModel.ForecastData>>()
    private val isLoadingObserver = mock<Observer<Boolean?>>()
    private val errorsObserver = mock<Observer<MainViewModel.Error?>>()

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = buildViewModel()
    }

    @Test
    fun getWeatherDataTest() {
        val currentWeather = ForecastElementFactory.build()
        val fiveDayForecast = listOf(DayForecastFactory.build(), DayForecastFactory.build())

        testCoroutineRule.runBlockingTest {

            whenever(getWeatherForecast.currentWeather(any())).thenReturn(
                Operation.Success(currentWeather)
            )
            whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
                Operation.Success(fiveDayForecast)
            )

            observeWeatherData()
        }

        viewModel.onRefresh()
        assertNotNull(viewModel.combinedForecastData().value)
        assertEquals(currentWeather, viewModel.combinedForecastData().value!!.currentWeather)
        assertThat(viewModel.combinedForecastData().value!!.todayForecast.keys.first())
            .isEqualTo("19")
        assertThat(viewModel.combinedForecastData().value!!.todayForecast.values.first())
            .isEqualTo("09")
        assertEquals(
            fiveDayForecast.drop(1),
            viewModel.combinedForecastData().value!!.fiveDayForecast
        )
    }

    @Test
    fun isLoadingTest() {
        testCoroutineRule.runBlockingTest {

            whenever(getWeatherForecast.currentWeather(any())).thenReturn(
                Operation.Success(ForecastElementFactory.build())
            )
            whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
                Operation.Success(listOf(DayForecastFactory.build(), DayForecastFactory.build()))
            )

            observeWeatherData()
            observeIsLoading()
        }

        assertThat(viewModel.isLoading().value).isNull()
        viewModel.onRefresh()
        verify(isLoadingObserver).onChanged(true)
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun errorFetchingCurrentWeatherTest() {
        testCoroutineRule.runBlockingTest {

            whenever(getWeatherForecast.currentWeather(any())).thenReturn(
                Operation.Error(Exception())
            )
            whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
                Operation.Success(listOf(DayForecastFactory.build(), DayForecastFactory.build()))
            )

            observeWeatherData()
            observeIsLoading()
            observeErrors()
        }

        assertThat(viewModel.errors().value).isNull()
        viewModel.onRefresh()
        assertNotNull(viewModel.errors().value)
        assertThat(viewModel.errors().value!!.errorName).isEqualTo(MainViewModel.ErrorName.NetworkError)
    }

    @Test
    fun errorFetchingFiveDayForecastTest() {
        testCoroutineRule.runBlockingTest {

            whenever(getWeatherForecast.currentWeather(any())).thenReturn(
                Operation.Success(ForecastElementFactory.build())
            )
            whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
                Operation.Error(Exception())
            )

            observeWeatherData()
            observeIsLoading()
            observeErrors()
        }

        assertThat(viewModel.errors().value).isNull()
        viewModel.onRefresh()
        assertNotNull(viewModel.errors().value)
        assertThat(viewModel.errors().value!!.errorName).isEqualTo(MainViewModel.ErrorName.NetworkError)
    }

    @Test
    fun errorNoAvailableDataTest() {
        testCoroutineRule.runBlockingTest {

            whenever(getWeatherForecast.currentWeather(any())).thenReturn(
                Operation.Success(ForecastElementFactory.build())
            )
            whenever(getWeatherForecast.fiveDayForecast(any())).thenReturn(
                Operation.Success(emptyList())
            )

            observeWeatherData()
            observeIsLoading()
            observeErrors()
        }

        assertThat(viewModel.errors().value).isNull()
        viewModel.onRefresh()
        assertNotNull(viewModel.errors().value)
        assertThat(viewModel.errors().value!!.errorName).isEqualTo(MainViewModel.ErrorName.NoDataAvailable)
    }

    @After
    fun tearDown() {
        removeObservers()
    }

    // Helpers

    private fun observeWeatherData() {
        viewModel.combinedForecastData().observeForever(forecastDataObserver)
    }

    private fun observeIsLoading() {
        viewModel.isLoading().observeForever(isLoadingObserver)
    }

    private fun observeErrors() {
        viewModel.errors().observeForever(errorsObserver)
    }

    private fun removeObservers() {
        viewModel.combinedForecastData().removeObserver(forecastDataObserver)
        viewModel.isLoading().removeObserver(isLoadingObserver)
        viewModel.errors().removeObserver(errorsObserver)
    }

    private fun buildViewModel() = MainViewModel(getWeatherForecast)
}