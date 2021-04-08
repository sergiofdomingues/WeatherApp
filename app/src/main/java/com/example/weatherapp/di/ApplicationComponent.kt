package com.example.weatherapp.di

import com.example.weatherapp.WeatherApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        GsonModule::class
    ]
)
interface ApplicationComponent {

    fun inject(app: WeatherApplication)
    fun activityComponent(): ActivityComponent
}
