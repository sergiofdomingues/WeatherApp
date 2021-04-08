package com.example.weatherapp.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.BaseActivity
import com.example.weatherapp.R
import com.example.weatherapp.adapter.FiveDayForecastAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.DayForecast
import com.example.weatherapp.model.ForecastElement
import com.example.weatherapp.rx.AutoDisposable
import com.example.weatherapp.util.ViewModelFactory
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.MainViewModel.ErrorName.NetworkError
import com.example.weatherapp.viewmodel.MainViewModel.ErrorName.NoDataAvailable
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.XAxisValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var autoDisposable: AutoDisposable

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
        component.inject(this)
        autoDisposable.bindTo(this.lifecycle)

        binding.dailyWeatherForecast.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        viewModel.init()

        viewModel
            .currentWeather()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setCurrentWeatherData(it)
            }
            .addToAutoDisposable()

        viewModel
            .todayForecastChartData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                drawChart(it)
            }
            .addToAutoDisposable()

        viewModel
            .fiveDayForecast()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setFiveDayForecastData(it)
            }
            .addToAutoDisposable()

        viewModel
            .errors()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showError(it)
            }
            .addToAutoDisposable()

        viewModel
            .isLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.container.isRefreshing = it
            }
            .addToAutoDisposable()

        binding.container
            .refreshes()
            .subscribe {
                viewModel.onRefresh()
            }
            .addToAutoDisposable()

        binding.container.isRefreshing = true
    }

    override fun onDestroy() {
        viewModel.onCleared()
        super.onDestroy()
    }

    private fun setFiveDayForecastData(fiveDayForecast: List<DayForecast>) {
        binding.dailyWeatherForecast.adapter = FiveDayForecastAdapter(fiveDayForecast)
    }

    private fun setCurrentWeatherData(weather: ForecastElement) {
        binding.todayDegrees.text = "${weather.temperature}\u00B0"
        binding.nowIcon.load(weather.iconUrl)
    }

    private fun drawChart(tempHour: Map<String, String>) {
        setupChartSettings()

        // Hours (x axis data)
        val xAxisLabels = mutableListOf<String>()
        tempHour.values.forEach { xAxisLabels.add(it) }

        // Temperatures (y axis data)
        val lineEntry = mutableListOf<Entry>()
        tempHour.keys.forEachIndexed { index, temp ->
            lineEntry.add(
                Entry(temp.toFloat(), index)
            )
        }

        val lineDataSet = LineDataSet(lineEntry, "Temperature")
        lineDataSet.color = ContextCompat.getColor(this, R.color.DodgerBlue)
        lineDataSet.lineWidth = 4f
        lineDataSet.setDrawCubic(true)

        val data = LineData(xAxisLabels, lineDataSet)
        binding.todayForecastChart.data = data
        binding.todayForecastChart.animateXY(1000, 1000)
    }

    private fun setupChartSettings() {
        // X axis
        binding.todayForecastChart.xAxis.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelRotationAngle = 315f
            valueFormatter = ClaimsXAxisValueFormatter()
            setDrawLimitLinesBehindData(false)
        }

        // Y right axis
        binding.todayForecastChart.axisRight.apply {
            removeAllLimitLines()
            valueFormatter = ClaimsYAxisValueFormatter()
        }

        // Y left axis
        binding.todayForecastChart.axisLeft.apply {
            setDrawLabels(false)
            removeAllLimitLines()
            isEnabled = false
        }

        binding.todayForecastChart.apply {
            setDescription(null)
            setScaleEnabled(false)
            legend.position = Legend.LegendPosition.ABOVE_CHART_LEFT
        }
    }

    private fun showError(error: MainViewModel.Error) {
        when (error.errorName) {
            NoDataAvailable -> messageManager.showError(R.string.error_no_forecast_data)
            NetworkError -> messageManager.showError(error.message)
        }
    }

    private fun Disposable.addToAutoDisposable() {
        autoDisposable.add(this)
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