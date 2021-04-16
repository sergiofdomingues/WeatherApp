package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.util.MessageManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    @Inject lateinit var messageManager: MessageManager
}