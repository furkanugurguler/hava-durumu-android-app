package com.example.havadurumu.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class DailyForecastAdapter(private val forecastList: List<ForecastItem>) :
    RecyclerView.Adapter<DailyForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.tvTime)
        val temp: TextView = view.findViewById(R.id.tvTemp)
        val icon: ImageView = view.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList[position]
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        holder.time.text = sdf.format(item.dt * 1000L)
        val tempCelsius = (item.main.temp - 273.15).roundToInt()
        holder.temp.text = "${tempCelsius}°C"

        val iconRes = when (item.weather[0].icon) {
            "01d", "01n" -> R.drawable.sunny
            "02d", "03d", "04d" -> R.drawable.cloudy
            "02n", "03n", "04n" -> R.drawable.nightcloudy
            "09d", "09n", "10d", "10n" -> R.drawable.rainy
            "11d", "11n" -> R.drawable.stormyrain
            "13d", "13n" -> R.drawable.snowy
            else -> R.drawable.cloudy
        }
        holder.icon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int = forecastList.size
}