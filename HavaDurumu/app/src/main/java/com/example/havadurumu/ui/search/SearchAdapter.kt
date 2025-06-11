package com.example.havadurumu.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.model.WeatherResponse
import kotlin.math.roundToInt

class SearchAdapter(
    private val weatherList: MutableList<WeatherResponse>,
    private val onFavoriteClick: (WeatherResponse) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val city: TextView = view.findViewById(R.id.tvCity)
        val temp: TextView = view.findViewById(R.id.tvTemp)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val humidity: TextView = view.findViewById(R.id.tvHumidity)
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val favoriteIcon: ImageView = view.findViewById(R.id.imgFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = weatherList[position]
        val tempCelsius = item.main.temp.roundToInt()
        holder.city.text = "${item.name}, ${item.sys.country}"
        holder.temp.text = "${tempCelsius}°C"
        holder.desc.text = item.weather[0].description.replaceFirstChar { it.uppercase() }
        holder.humidity.text = "Nem: ${item.main.humidity}%"

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

        holder.favoriteIcon.setImageResource(R.drawable.ic_star)
        holder.favoriteIcon.setOnClickListener {
            onFavoriteClick(item)
        }
    }

    override fun getItemCount(): Int = weatherList.size

    fun updateData(newList: List<WeatherResponse>) {
        weatherList.clear()
        weatherList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): WeatherResponse {
        return weatherList[position]
    }
}