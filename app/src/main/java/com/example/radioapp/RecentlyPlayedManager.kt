package com.example.radioapp

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class RecentlyPlayedManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("recently_played", Context.MODE_PRIVATE)
    
    var recentStations by mutableStateOf(loadRecentStations())
        private set
    
    private fun loadRecentStations(): List<String> {
        val recent = sharedPreferences.getStringSet("recent_stations", emptySet()) ?: emptySet()
        // Convert set back to list with preserved order
        val recentList = sharedPreferences.getString("recent_order", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        return recentList.take(5) // Keep only last 5
    }
    
    fun addRecentStation(stationName: String) {
        val newRecent = recentStations.toMutableList()
        // Remove if already exists to avoid duplicates
        newRecent.remove(stationName)
        // Add to beginning
        newRecent.add(0, stationName)
        // Keep only last 5
        recentStations = newRecent.take(5)
        saveRecentStations()
    }
    
    private fun saveRecentStations() {
        sharedPreferences.edit()
            .putStringSet("recent_stations", recentStations.toSet())
            .putString("recent_order", recentStations.joinToString(","))
            .apply()
    }
    
    fun getRecentStations(): Map<String, String> {
        return radioStations.filter { recentStations.contains(it.key) }
            .toList()
            .sortedBy { recentStations.indexOf(it.first) }
            .toMap()
    }
}