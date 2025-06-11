package com.example.havadurumu.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.havadurumu.R
import com.example.havadurumu.model.WeatherResponse
import com.example.havadurumu.ui.search.SearchAdapter
import com.example.havadurumu.viewmodel.WeatherViewModel

class FavoriteFragment : Fragment() {

    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var tvFavoritesTitle: TextView
    private lateinit var viewModel: WeatherViewModel
    private lateinit var favoritesAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites)
        tvFavoritesTitle = view.findViewById(R.id.tvFavoritesTitle)

        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        favoritesAdapter = SearchAdapter(mutableListOf()) { city -> removeFromFavorites(city) }
        recyclerFavorites.adapter = favoritesAdapter

        viewModel.favoriteCities.observe(viewLifecycleOwner) { favorites ->
            favoritesAdapter.updateData(favorites)
            updateFavoritesVisibility(favorites)
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val city = favoritesAdapter.getItem(position)
                removeFromFavorites(city)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerFavorites)

        viewModel.loadFavoritesFromPreferences(requireContext())
    }

    private fun removeFromFavorites(city: WeatherResponse) {
        viewModel.removeFromFavorites(city)
        viewModel.saveFavoritesToPreferences(requireContext())
        Toast.makeText(requireContext(), "${city.name} Favorilerden kaldırıldı!", Toast.LENGTH_SHORT).show()
    }

    private fun updateFavoritesVisibility(favorites: List<WeatherResponse>) {
        tvFavoritesTitle.visibility = View.VISIBLE
        recyclerFavorites.visibility = if (favorites.isNotEmpty()) View.VISIBLE else View.GONE
    }
}