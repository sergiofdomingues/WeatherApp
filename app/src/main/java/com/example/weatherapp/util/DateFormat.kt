package com.example.weatherapp.util

import com.example.weatherapp.model.ForecastElement
import java.text.SimpleDateFormat
import java.util.*

object DateFormat {

    private fun toDay(originalDateStr: String, formatterFrom: SimpleDateFormat): String {
        val formatterTo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatterFrom.parse(originalDateStr)?.let { formatterTo.format(it) } ?: ""
    }

    private fun toHour(originalDateStr: String, formatterFrom: SimpleDateFormat): String {
        val formatterTo = SimpleDateFormat("HH", Locale.getDefault())
        return formatterFrom.parse(originalDateStr)?.let { formatterTo.format(it) } ?: ""
    }

    private fun toReadableDate(originalDateStr: String, formatterFrom: SimpleDateFormat): String {
        val formatterTo = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        return formatterFrom.parse(originalDateStr)?.let { formatterTo.format(it) } ?: ""
    }

    fun formatDate(originalDateStr: String): ForecastElement.DateTimeInfo {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return ForecastElement.DateTimeInfo(
            toDay(originalDateStr, formatterFrom),
            toHour(originalDateStr, formatterFrom),
            toReadableDate(originalDateStr, formatterFrom)
        )
    }
}