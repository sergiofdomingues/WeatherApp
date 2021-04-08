package com.example.weatherapp.api

import com.example.weatherapp.api.response.FiveDayForecastResponse
import com.example.weatherapp.api.response.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastService {

    @GET("data/2.5/weather?")
    fun getCurrentWeather(
        @Query("q") city: String?,
        @Query("APPID") app_id: String? = APIKEY,
        @Query("units") units: String? = "metric"
    ): Single<WeatherResponse>

    @GET("data/2.5/forecast?")
    fun getFiveDayForecast(
        @Query("q") city: String?,
        @Query("APPID") app_id: String? = APIKEY,
        @Query("units") units: String? = "metric"
    ): Single<FiveDayForecastResponse>

    companion object {
        private const val APIKEY = "176ef3b47d7004852bd7766e7741b361"
    }
}