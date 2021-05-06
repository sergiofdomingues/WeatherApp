package com.example.weatherapp.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.framework.database.WeatherDatabase.Companion.DB_VERSION

@Database(
    entities = [NowWeatherDbo::class, FiveDayElementDbo::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        const val DB_VERSION = 4
    }
}