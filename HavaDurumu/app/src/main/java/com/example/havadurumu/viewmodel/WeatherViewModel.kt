package com.example.havadurumu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.havadurumu.api.RetrofitInstance
import com.example.havadurumu.model.ForecastResponse
import com.example.havadurumu.model.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    private val _forecastLiveData = MutableLiveData<ForecastResponse>()
    val forecastLiveData: LiveData<ForecastResponse> get() = _forecastLiveData

    private val _favoriteCities = MutableLiveData<MutableList<WeatherResponse>>(mutableListOf())
    val favoriteCities: LiveData<MutableList<WeatherResponse>> get() = _favoriteCities

    private val weatherCache = mutableMapOf<String, WeatherResponse>()

    fun fetchWeather(city: String, onSuccess: (WeatherResponse) -> Unit, onError: () -> Unit) {
        weatherCache[city]?.let {
            onSuccess(it)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, "a71190f7c8c308e35f60a917596f9c21")
                weatherCache[city] = response
                withContext(Dispatchers.Main) {
                    onSuccess(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getWeatherByCoordinates(lat, lon, "a71190f7c8c308e35f60a917596f9c21")
                _weather.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchForecast(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getForecast(lat, lon, "a71190f7c8c308e35f60a917596f9c21")
                _forecastLiveData.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToFavorites(city: WeatherResponse) {
        val currentFavorites = _favoriteCities.value ?: mutableListOf()
        if (!currentFavorites.contains(city)) {
            currentFavorites.add(city)
            _favoriteCities.postValue(currentFavorites)
        }
    }

    fun removeFromFavorites(city: WeatherResponse) {
        val currentFavorites = _favoriteCities.value ?: mutableListOf()
        currentFavorites.remove(city)
        _favoriteCities.postValue(currentFavorites)
    }

    fun loadFavoritesFromPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("favorite_cities", null)
        val type = object : TypeToken<List<WeatherResponse>>() {}.type
        val savedFavorites: List<WeatherResponse>? = gson.fromJson(json, type)
        if (savedFavorites != null) {
            _favoriteCities.postValue(savedFavorites.toMutableList())
        }
    }

    fun saveFavoritesToPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(_favoriteCities.value)
        editor.putString("favorite_cities", json)
        editor.apply()
    }
} 