package com.example.havadurumu.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.api.RetrofitInstance
import com.example.havadurumu.model.ForecastItem
import com.example.havadurumu.model.WeatherResponse
import com.example.havadurumu.ui.forecast.HourlyForecastAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.havadurumu.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private lateinit var tvLocation: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvDesc: TextView
    private lateinit var imgIcon: ImageView
    private lateinit var recyclerHourlyForecast: RecyclerView
    private lateinit var tvSunrise: TextView
    private lateinit var tvSunset: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherViewModel: WeatherViewModel
    private val hourlyForecastList = mutableListOf<ForecastItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLocation = view.findViewById(R.id.tvLocation)
        tvTemp = view.findViewById(R.id.tvTemp)
        tvDesc = view.findViewById(R.id.tvDesc)
        imgIcon = view.findViewById(R.id.imgIcon)
        recyclerHourlyForecast = view.findViewById(R.id.recyclerHourlyForecast)
        tvSunrise = view.findViewById(R.id.tvSunrise)
        tvSunset = view.findViewById(R.id.tvSunset)
        tvHumidity = view.findViewById(R.id.tvHumidity)

        recyclerHourlyForecast.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerHourlyForecast.adapter = HourlyForecastAdapter(hourlyForecastList)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        getLocation()
        observeWeather()
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    weatherViewModel.fetchWeather(it.latitude, it.longitude)
                    fetchHourlyForecast(it.latitude, it.longitude)
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
    }

    private fun fetchHourlyForecast(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getForecast(lat, lon, "a71190f7c8c308e35f60a917596f9c21")
                withContext(Dispatchers.Main) {
                    hourlyForecastList.clear()
                    hourlyForecastList.addAll(response.list.take(5))
                    recyclerHourlyForecast.adapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun observeWeather() {
        weatherViewModel.weather.observe(viewLifecycleOwner) { weather ->
            tvLocation.text = "${weather.name}, ${weather.sys.country}"
            val tempCelsius = (weather.main.temp - 273.15).roundToInt()
            tvTemp.text = "${tempCelsius}°C"
            tvDesc.text = weather.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }.replace("Scattered", "Dağınık").replace("clouds", "Bulutlar")

            val iconRes = when (weather.weather[0].icon) {
                "01d", "01n" -> R.drawable.sunny
                "02d", "03d", "04d" -> R.drawable.cloudy
                "02n", "03n", "04n" -> R.drawable.nightcloudy
                "09d", "09n", "10d", "10n" -> R.drawable.rainy
                "11d", "11n" -> R.drawable.stormyrain
                "13d", "13n" -> R.drawable.snowy
                else -> R.drawable.cloudy
            }
            imgIcon.setImageResource(iconRes)

            tvHumidity.text = "Nem: ${weather.main.humidity}%"
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvSunrise.text = "Güneş Doğuş: ${sdf.format(weather.sys.sunrise * 1000L)}"
            tvSunset.text = "Güneş Batış: ${sdf.format(weather.sys.sunset * 1000L)}"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }
}