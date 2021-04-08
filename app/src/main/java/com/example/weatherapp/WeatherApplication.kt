package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.ApplicationComponent
import com.example.weatherapp.di.ApplicationModule
import com.example.weatherapp.di.DaggerApplicationComponent

open class WeatherApplication : Application() {
    open val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this)).build()
    }
}