package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemDayForecastBinding
import com.example.weatherapp.domain.model.DayForecast

class FiveDayForecastAdapter(
    private val days: List<DayForecast>
) : RecyclerView.Adapter<FiveDayForecastAdapter.ViewHolder>() {

    private lateinit var binding: ItemDayForecastBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemDayForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(day: DayForecast) = with(itemView) {
            binding.title.text = day.readableDate
            binding.hourWeatherList.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = HourWeatherListAdapter(day.hourlyForecastList)
            }
        }
    }
}