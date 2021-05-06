package com.example.weatherapp.presentation

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.presentation.ui.MessageManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    @Inject lateinit var messageManager: MessageManager
}