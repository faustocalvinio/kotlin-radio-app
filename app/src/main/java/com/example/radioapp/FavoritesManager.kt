package com.example.radioapp

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class FavoritesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("radio_favorites", Context.MODE_PRIVATE)
    
    var favorites by mutableStateOf(loadFavorites())
        private set
    
    private fun loadFavorites(): Set<String> {
        return sharedPreferences.getStringSet("favorite_stations", emptySet()) ?: emptySet()
    }
    
    fun isFavorite(stationName: String): Boolean {
        return favorites.contains(stationName)
    }
    
    fun toggleFavorite(stationName: String) {
        val newFavorites = favorites.toMutableSet()
        if (newFavorites.contains(stationName)) {
            newFavorites.remove(stationName)
        } else {
            newFavorites.add(stationName)
        }
        favorites = newFavorites
        saveFavorites()
    }
    
    private fun saveFavorites() {
        sharedPreferences.edit()
            .putStringSet("favorite_stations", favorites)
            .apply()
    }
    
    fun getFavoriteStations(): Map<String, String> {
        return radioStations.filter { favorites.contains(it.key) }
    }
}