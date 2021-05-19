package com.example.weatherapp.di

import com.example.weatherapp.framework.WeatherConditionsDataSourceImp
import com.example.weatherapp.data.WeatherConditionsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class WeatherConditionsDataSourceModule {

    @Binds
    abstract fun bindWeatherConditionsDataSource(dataSourceImp: WeatherConditionsDataSourceImp): WeatherConditionsDataSource
}