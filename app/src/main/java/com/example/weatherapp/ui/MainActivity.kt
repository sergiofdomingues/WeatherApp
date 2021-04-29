package com.example.weatherapp.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.viewmodel.MainViewModel
import com.example.weatherapp.domain.viewmodel.MainViewModel.ErrorName.NetworkError
import com.example.weatherapp.domain.viewmodel.MainViewModel.ErrorName.NoDataAvailable
import com.example.weatherapp.domain.viewmodel.MainViewModel.ForecastData
import com.example.weatherapp.domain.model.DayForecast
import com.example.weatherapp.domain.model.ForecastElement
import com.example.weatherapp.ui.adapter.FiveDayForecastAdapter
import com.example.weatherapp.ui.chart.LineChartBuilder
import com.example.weatherapp.utils.ViewModelFactory
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.XAxisValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
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

        viewModel.weatherConditions.observe(this) { forecastData ->
            forecastData?.let { setupWeatherForecastData(it) }
        }

        viewModel.isLoading.observe(this) {
            binding.container.isRefreshing = it
        }

        viewModel.errors.observe(this) { error ->
            error?.let { showError(it) }
        }
    }

    private fun setupWeatherForecastData(forecast: ForecastData) {
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

    private fun showError(error: MainViewModel.Error) {
        when (error.errorName) {
            NoDataAvailable -> messageManager.showError(R.string.error_no_forecast_data)
            NetworkError -> messageManager.showError(error.message)
        }
    }

    class ClaimsYAxisValueFormatter : YAxisValueFormatter {
        override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
            return "${value.toInt()}\u00B0"
        }
    }

    class ClaimsXAxisValueFormatter : XAxisValueFormatter {
        override fun getXValue(
            original: String?, index: Int, viewPortHandler: ViewPortHandler?
        ): String {
            return "${original}h"
        }
    }
}