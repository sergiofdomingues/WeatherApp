package com.example.weatherapp.di

import com.example.weatherapp.ui.MainActivity
import dagger.Subcomponent

@Subcomponent
interface ActivityComponent {

    fun inject(activity: MainActivity)
}