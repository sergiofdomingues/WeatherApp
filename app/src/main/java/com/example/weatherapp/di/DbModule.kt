package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.framework.db.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ): WeatherDatabase =
        Room.databaseBuilder(app, WeatherDatabase::class.java, "databaseweather")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideWeatherDao(weatherDatabase: WeatherDatabase) =
        weatherDatabase.weatherDao()
}
