package com.example.weatherapp.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.ui.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import javax.inject.Inject

class LineChartBuilder @Inject constructor(private val context: Context) {

    fun drawChart(tempHour: Map<String, String>, lineChart: LineChart) {
        setupChartSettings(lineChart)
        lineChart.show(buildChartData(tempHour))
    }

    private fun LineChart.show(chartData: LineData) {
        data = chartData
        invalidate()
    }

    private fun buildChartData(tempHour: Map<String, String>): LineData {
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
            valueFormatter = MainActivity.ClaimsXAxisValueFormatter()
            setDrawLimitLinesBehindData(false)
        }

        // Y right axis
        lineChart.axisRight.apply {
            removeAllLimitLines()
            valueFormatter = MainActivity.ClaimsYAxisValueFormatter()
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
}