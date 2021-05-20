package com.example.weatherapp.framework.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.weatherapp.domain.DayForecast
import com.example.weatherapp.domain.ForecastElement
import com.example.weatherapp.domain.WeatherForecast
import com.example.weatherapp.framework.network.FiveDayForecastResponse
import com.example.weatherapp.framework.network.WeatherResponse
import com.example.weatherapp.presentation.utils.DateFormat.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(tableName = "now_weather")
data class NowWeatherDbo(
    val iconUrl: String,
    val temperature: Int,
    val dateStr: String,
    @PrimaryKey val nowId: Long? = 0
)

@Entity(
    tableName = "five_day_elements",
    foreignKeys = [ForeignKey(
        entity = NowWeatherDbo::class,
        parentColumns = ["nowId"],
        childColumns = ["ownerId"]
    )]
)
data class FiveDayElementDbo constructor(
    val ownerId: Long,
    val iconUrl: String,
    val temperature: Int,
    val dateStr: String,
    @PrimaryKey(autoGenerate = true) val id: Long? = null
)

/** Convert database objects to domain **/

suspend fun asUiData(
    nowDbo: NowWeatherDbo,
    fiveDayElementsDbo: List<FiveDayElementDbo>
) = withContext(Dispatchers.Default) {
    val listOfDays = fiveDayElementsDbo.map { it.toDomainModel() }.toListOfDays()
    WeatherForecast(
        currentWeather = ForecastElement(
            nowDbo.iconUrl,
            nowDbo.temperature,
            if (nowDbo.dateStr.isEmpty()) null else formatDate(nowDbo.dateStr)
        ),
        todayForecast = if (listOfDays.isNotEmpty()) listOfDays.firstOrNull()
            ?.mapToChartData() else null,
        fiveDayForecast = if (listOfDays.isNotEmpty()) listOfDays.drop(1) else emptyList()
    )
}

/** Mapping to Map<Hour, Temperature> **/
private fun DayForecast.mapToChartData() =
    hourlyForecastList.map { (it.dateTimeInfo?.hourStr ?: "") to it.temperature.toString() }
        .toMap()

private fun FiveDayElementDbo.toDomainModel() =
    ForecastElement(iconUrl, temperature, formatDate(dateStr))

private fun List<ForecastElement>.toListOfDays(): List<DayForecast> =
    // Grouping forecast elements by day
    groupBy { weatherData -> weatherData.dateTimeInfo?.dayStr }.entries.map {
        DayForecast(
            readableDate = it.value.first().dateTimeInfo?.readableDate ?: "",
            hourlyForecastList = it.value
        )
    }

/** Convert domain to database objects **/

fun asNowWeatherDBModel(
    nowResponse: WeatherResponse
) = NowWeatherDbo(
    iconUrl = nowResponse.weather.first().icon?.let { "http://openweathermap.org/img/w/$it.png" }
        ?: "",
    temperature = nowResponse.main?.temp?.toInt() ?: 0,
    dateStr = nowResponse.dateString ?: ""
)

suspend fun asFiveDayWeatherElementsDBModel(
    nowId: Long,
    fiveDayResponse: FiveDayForecastResponse
) = withContext(Dispatchers.Default) {
    fiveDayResponse.forecastWeatherElements!!.map { response ->
        FiveDayElementDbo(
            ownerId = nowId,
            iconUrl = response.weather.first().icon?.let { "http://openweathermap.org/img/w/$it.png" }
                ?: "",
            temperature = response.main?.temp?.toInt() ?: 0,
            dateStr = response.dateString ?: ""
        )
    }
}