package com.example.havadurumu.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.model.WeatherResponse
import com.example.havadurumu.ui.WeatherAnimationView
import com.example.havadurumu.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerSearchResults: RecyclerView
    private lateinit var layoutSearchContainer: LinearLayout
    private lateinit var weatherAnimationView: WeatherAnimationView
    private lateinit var progressBar: ProgressBar
    private val searchResults = mutableListOf<WeatherResponse>()
    private var searchJob: Job? = null
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        etSearch = view.findViewById(R.id.etSearch)
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults)
        layoutSearchContainer = view.findViewById(R.id.layout_search_container)
        weatherAnimationView = view.findViewById(R.id.weather_animation_view)
        progressBar = view.findViewById(R.id.progressBar)
        val ivClearSearch: ImageView = view.findViewById(R.id.ivClearSearch)

        recyclerSearchResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerSearchResults.adapter = SearchAdapter(searchResults) { city -> addToFavorites(city) }

        viewModel.loadFavoritesFromPreferences(requireContext())

        if (savedInstanceState == null) {
            searchWeather("Istanbul")
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                ivClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    if (query.length >= 3) {
                        searchWeather(query)
                    } else {
                        searchResults.clear()
                        recyclerSearchResults.adapter?.notifyDataSetChanged()
                        weatherAnimationView.setWeatherType(null)
                        updateBackground(null)
                    }
                }
            }
        })

        ivClearSearch.setOnClickListener {
            etSearch.text.clear()
        }
    }

    private fun searchWeather(query: String) {
        searchResults.clear()
        progressBar.visibility = View.VISIBLE
        viewModel.fetchWeather(
            city = query,
            onSuccess = { response ->
                searchResults.add(response)
                recyclerSearchResults.adapter?.notifyDataSetChanged()
                updateBackground(response)
                val weatherMain = response.weather.getOrNull(0)?.main ?: "Clear"
                weatherAnimationView.setWeatherType(weatherMain)
                progressBar.visibility = View.GONE
            },
            onError = {
                searchResults.clear()
                recyclerSearchResults.adapter?.notifyDataSetChanged()
                weatherAnimationView.setWeatherType(null)
                updateBackground(null)
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Şehir bulunamadı, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateBackground(weather: WeatherResponse?) {
        if (weather?.weather?.isNotEmpty() == true) {
            val backgroundResource = when (weather.weather[0].main) {
                "Rain" -> R.drawable.rain_background_gradient
                "Snow" -> R.drawable.snow_background_gradient
                "Clear" -> R.drawable.clear_background_gradient
                "Clouds", "Few clouds", "Scattered clouds", "Broken clouds" -> R.drawable.cloudy_background_gradient
                "Thunderstorm" -> R.drawable.thunderstorm_background_gradient
                else -> R.drawable.gradient_background
            }
            layoutSearchContainer.background = ContextCompat.getDrawable(requireContext(), backgroundResource)
        } else {
            layoutSearchContainer.background = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_background)
        }
    }

    private fun addToFavorites(city: WeatherResponse) {
        viewModel.addToFavorites(city)
        viewModel.saveFavoritesToPreferences(requireContext())
        Toast.makeText(requireContext(), "${city.name} Favorilere eklendi!", Toast.LENGTH_SHORT).show()
    }
}