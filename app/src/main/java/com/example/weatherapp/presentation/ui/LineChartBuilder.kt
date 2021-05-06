package com.example.weatherapp.presentation.ui

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.XAxisValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LineChartBuilder @Inject constructor(
    @ActivityContext private val context: Context
) {

    fun drawChart(hourTemp: Map<String, String>, lineChart: LineChart) {
        setupChartSettings(lineChart)
        lineChart.show(buildChartData(hourTemp))
    }

    private fun LineChart.show(chartData: LineData) {
        data = chartData
        invalidate()
    }

    private fun buildChartData(hourTemp: Map<String, String>): LineData {
        // Hours (x axis data)
        val xAxisLabels = mutableListOf<String>()
        hourTemp.keys.forEach { xAxisLabels.add(it) }

        // Temperatures (y axis data)
        val lineEntry = mutableListOf<Entry>()
        hourTemp.values.forEachIndexed { index, temp ->
            lineEntry.add(
                Entry(temp.toFloat(), index)
            )
        }

        val lineDataSet = LineDataSet(lineEntry, context.getString(R.string.temperature_title))
        lineDataSet.color = ContextCompat.getColor(context, R.color.DodgerBlue)
        lineDataSet.lineWidth = 4f
        lineDataSet.setDrawCubic(true)

        return LineData(xAxisLabels, lineDataSet)
    }

    private fun setupChartSettings(lineChart: LineChart) {
        // X axis
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelRotationAngle = 315f
            valueFormatter = ClaimsXAxisValueFormatter()
            setDrawLimitLinesBehindData(false)
        }

        // Y right axis
        lineChart.axisRight.apply {
            removeAllLimitLines()
            valueFormatter = ClaimsYAxisValueFormatter()
        }

        // Y left axis
        lineChart.axisLeft.apply {
            setDrawLabels(false)
            removeAllLimitLines()
            isEnabled = false
        }

        lineChart.apply {
            setDescription(null)
            setScaleEnabled(false)
            legend.position = Legend.LegendPosition.ABOVE_CHART_LEFT
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