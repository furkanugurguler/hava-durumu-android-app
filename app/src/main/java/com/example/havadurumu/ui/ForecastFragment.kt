package com.example.havadurumu.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.api.RetrofitInstance
import com.example.havadurumu.model.ForecastResponse
import com.example.havadurumu.ui.forecast.DailyForecastAdapter
import com.example.havadurumu.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ForecastFragment : Fragment() {

    private lateinit var recyclerForecast: RecyclerView
    private lateinit var tvForecastTitle: TextView
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_forecast, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerForecast = view.findViewById(R.id.recyclerForecast)
        tvForecastTitle = view.findViewById(R.id.tvForecastTitle)
        tvForecastTitle.text = "5 Günlük Tahmin"

        recyclerForecast.layoutManager = LinearLayoutManager(requireContext())
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        fetchForecast()
    }

    private fun fetchForecast() {
        val lat = 39.9255
        val lon = 32.8663

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getForecast(lat, lon, "a71190f7c8c308e35f60a917596f9c21")
                withContext(Dispatchers.Main) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Calendar.getInstance().time)
                    val dailyForecast = response.list.groupBy {
                        it.dtTxt.split(" ")[0]
                    }.map { it.value.first() }
                        .filter { it.dtTxt.split(" ")[0] != today }
                        .take(5)
                    recyclerForecast.adapter = DailyForecastAdapter(dailyForecast)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}