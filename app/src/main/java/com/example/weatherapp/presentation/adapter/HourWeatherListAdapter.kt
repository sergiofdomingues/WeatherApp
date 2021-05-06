package com.example.weatherapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHourTempForecastBinding
import com.example.weatherapp.domain.model.ForecastElement

class HourWeatherListAdapter(
    private val hours: List<ForecastElement>
) : RecyclerView.Adapter<HourWeatherListAdapter.ViewHolder>() {

    private lateinit var binding: ItemHourTempForecastBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemHourTempForecastBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount() = hours.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hourForecast: ForecastElement) = with(itemView) {
            binding.dayIcon.load(hourForecast.iconUrl)
            binding.dayDegrees.text =
                context.getString(R.string.temperature_in_degrees, hourForecast.temperature)
            binding.dayHour.text = hourForecast.dateTimeInfo?.hourStr ?: ""
        }
    }
}