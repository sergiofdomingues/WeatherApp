package com.example.weatherapp.presentation.weather

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.*
import com.example.weatherapp.presentation.BaseActivity
import com.example.weatherapp.domain.ErrorStatus.ErrorType.*
import com.example.weatherapp.presentation.utils.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var chartBuilder: LineChartBuilder

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainViewModel>
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            MainViewModel::class.java
        )
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dailyWeatherForecast.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.container.setOnRefreshListener { viewModel.onRefresh() }

        viewModel.weatherForecast.observe(this) { result ->
            result?.let {
                it.data?.let { forecastData -> setupWeatherForecastData(forecastData) }

                binding.container.isRefreshing = result is Resource.Loading && result.data != null
                binding.todayForecastChart.isVisible = !result.data?.todayForecast.isNullOrEmpty()
                if (result is Resource.Error) result.error?.let { error -> showError(error) }
            }
        }
    }

    private fun setupWeatherForecastData(forecast: WeatherForecast) {
        forecast.currentWeather?.let { setCurrentWeatherData(it) }
        forecast.todayForecast?.let { chartBuilder.drawChart(it, binding.todayForecastChart) }
        setFiveDayForecastData(forecast.fiveDayForecast)
    }

    private fun setFiveDayForecastData(fiveDayForecast: List<DayForecast>) {
        binding.dailyWeatherForecast.adapter = FiveDayForecastAdapter(fiveDayForecast)
    }

    private fun setCurrentWeatherData(weather: ForecastElement) {
        binding.todayDegrees.text =
            resources.getString(R.string.temperature_in_degrees, weather.temperature)
        binding.nowIcon.load(weather.iconUrl)
    }

    private fun showError(error: ErrorStatus) {
        when (error.errorType) {
            NowCallError, FiveDayCallError, UnknownError -> messageManager.showError(error.message)
            UnavailableNowWeather -> messageManager.showError(R.string.error_no_now_data)
            UnavailableFiveDayForecast -> messageManager.showError(R.string.error_no_five_day_forecast_data)
        }
    }
}