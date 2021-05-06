package com.example.weatherapp.framework.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastService {

    @GET("data/2.5/weather?")
    suspend fun getCurrentWeather(
        @Query("q") city: String?,
        @Query("APPID") app_id: String? = APIKEY,
        @Query("units") units: String? = "metric"
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast?")
    suspend fun getFiveDayForecast(
        @Query("q") city: String?,
        @Query("APPID") app_id: String? = APIKEY,
        @Query("units") units: String? = "metric"
    ): Response<FiveDayForecastResponse>

    companion object {
        private const val APIKEY = "176ef3b47d7004852bd7766e7741b361"
    }
}