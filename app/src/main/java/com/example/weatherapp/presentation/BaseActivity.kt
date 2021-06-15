package com.example.weatherapp.presentation

import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    @Inject lateinit var messageManager: MessageManager
}