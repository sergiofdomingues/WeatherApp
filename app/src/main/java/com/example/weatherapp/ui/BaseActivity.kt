package com.example.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.utils.MessageManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    @Inject lateinit var messageManager: MessageManager
}