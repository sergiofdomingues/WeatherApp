package com.example.weatherapp.framework.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(nowWeather: NowWeatherDbo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiveDayElements(elements: List<FiveDayElementDbo>): List<Long>

    @Query("select * from now_weather")
    fun getNowWeatherConditions(): Flow<NowWeatherDbo?>

    @Query("select * from five_day_elements where ownerId is :ownerId")
    fun getFiveDayWeatherElements(ownerId: Long): Flow<List<FiveDayElementDbo>?>

    @Transaction
    suspend fun clearAllTables() {
        deleteFiveDayElements()
        deleteNowWeather()
    }

    @Query("delete from now_weather")
    suspend fun deleteNowWeather()

    @Query("delete from five_day_elements")
    suspend fun deleteFiveDayElements()
}