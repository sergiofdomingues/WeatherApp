package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.util.MessageManager

abstract class BaseActivity : AppCompatActivity() {
    private val app get() = applicationContext as WeatherApplication
    val component by lazy { app.component.activityComponent() }
    internal val messageManager by lazy { MessageManager(this) }
}