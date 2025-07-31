package com.example.radioapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.example.radioapp.ui.theme.RadioAPPTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Slider
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.icons.filled.FilterList

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadioAPPTheme {
                RadioAppScreen()
            }
        }
    }
}

@Composable
fun RadioAppScreen() {
    val context = LocalContext.current
    var selectedUrl by remember { mutableStateOf<String?>(null) }
    var selectedRadio by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    var startedPlaying = false
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var selectedCountryFilter by remember { mutableStateOf("Todos") }
    var selectedGenreFilter by remember { mutableStateOf("Todos") }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    // Initialize managers
    val favoritesManager = remember { FavoritesManager(context) }
    val recentlyPlayedManager = remember { RecentlyPlayedManager(context) }
    val volumeManager = remember { VolumeManager(context) }
    val sleepTimerManager = remember { SleepTimerManager() }
    
    // Update volume when screen loads
    LaunchedEffect(Unit) {
        volumeManager.updateCurrentVolume()
    }
    
    // Filter stations based on search query and filters
    val filteredStations = remember(searchQuery, selectedCountryFilter, selectedGenreFilter) {
        var stations = if (searchQuery.isBlank()) {
            radioStations
        } else {
            radioStations.filter { 
                it.key.contains(searchQuery, ignoreCase = true) 
            }
        }
        
        // Apply country filter
        if (selectedCountryFilter != "Todos") {
            val stationsByCountry = getStationsByCountry()[selectedCountryFilter] ?: emptyList()
            stations = stations.filter { station ->
                stationsByCountry.any { it.name == station.key }
            }
        }
        
        // Apply genre filter
        if (selectedGenreFilter != "Todos") {
            val stationsByGenre = getStationsByGenre()[selectedGenreFilter] ?: emptyList()
            stations = stations.filter { station ->
                stationsByGenre.any { it.name == station.key }
            }
        }
        
        stations
    }
    
    // Get stations based on selected tab
    val displayStations = when (selectedTabIndex) {
        0 -> filteredStations
        1 -> favoritesManager.getFavoriteStations()
        2 -> recentlyPlayedManager.getRecentStations()
        else -> filteredStations
    }
    
    val tabs = listOf("Todas", "Favoritas", "Recientes")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(Color.Black)
        ) {
            // Current playing station
            selectedRadio?.let { radioName ->
                Text(
                    text = "♪ Reproduciendo: $radioName",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Play/Pause button
            if (startedPlaying) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = { isPlaying = !isPlaying },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color.Red else Color.Green
                    )
                ) {
                    Text(
                        text = if (isPlaying) "⏸ Pausar" else "▶ Reproducir",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                // Sleep Timer and Volume Controls Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sleep Timer Button
                    if (sleepTimerManager.isTimerActive) {
                        Button(
                            onClick = { sleepTimerManager.stopTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${sleepTimerManager.getFormattedTimeRemaining()}", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { showSleepTimerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Sleep Timer", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sleep Timer", color = Color.White)
                        }
                    }
                }
                
                // Volume control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Volumen",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = volumeManager.volume,
                        onValueChange = { volumeManager.setVolume(it) },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Green,
                            activeTrackColor = Color.Green,
                            inactiveTrackColor = Color.Gray
                        )
                    )
                    Text(
                        text = "${(volumeManager.volume * 100).toInt()}%",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            // Search bar with filter button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar emisoras...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Filter button
                IconButton(
                    onClick = { showFilterMenu = !showFilterMenu },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (selectedCountryFilter != "Todos" || selectedGenreFilter != "Todos") 
                                Color.Green 
                            else 
                                Color.Gray
                        )
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = Color.White
                    )
                }
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title,
                                color = if (selectedTabIndex == index) Color.Green else Color.White
                            ) 
                        }
                    )
                }
            }
            
            // Station list
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (displayStations.isEmpty()) {
                    Text(
                        text = when (selectedTabIndex) {
                            1 -> "No tienes estaciones favoritas aún"
                            2 -> "No has reproducido ninguna estación"
                            else -> "No se encontraron estaciones"
                        },
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                } else {
                    displayStations.forEach { (name, url) ->
                        // Find station info for additional details
                        val stationInfo = radioStationsData.find { it.name == name }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Station button with info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    onClick = {
                                        selectedUrl = url
                                        selectedRadio = name
                                        isPlaying = true
                                        startedPlaying = true
                                        recentlyPlayedManager.addRecentStation(name)
                                        Log.d("MainActivity", "Selected URL: $url")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedRadio == name) Color.Green else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                
                                // Station info
                                stationInfo?.let { info ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "🌍 ${info.country}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "🎵 ${info.genre}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Favorite button
                            IconButton(
                                onClick = { favoritesManager.toggleFavorite(name) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (favoritesManager.isFavorite(name)) Color.Red else Color.Gray
                                    )
                            ) {
                                Icon(
                                    imageVector = if (favoritesManager.isFavorite(name)) 
                                        Icons.Default.Favorite 
                                    else 
                                        Icons.Default.FavoriteBorder,
                                    contentDescription = if (favoritesManager.isFavorite(name)) 
                                        "Quitar de favoritos" 
                                    else 
                                        "Agregar a favoritos",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                
                // Extra space at bottom
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // ExoPlayer component
            selectedUrl?.let { url ->
                ExoPlayerView(context = context, url = url, isPlaying = isPlaying)
            }
        }
    }
    
    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Sleep Timer", color = Color.Black) },
            text = { Text("¿En cuántos minutos quieres que se detenga la reproducción?", color = Color.Black) },
            confirmButton = {
                Column {
                    val timerOptions = listOf(5, 10, 15, 30, 60)
                    timerOptions.forEach { minutes ->
                        TextButton(
                            onClick = {
                                sleepTimerManager.startTimer(minutes) {
                                    isPlaying = false
                                }
                                showSleepTimerDialog = false
                            }
                        ) {
                            Text("$minutes minutos")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Filter Menu
    if (showFilterMenu) {
        AlertDialog(
            onDismissRequest = { showFilterMenu = false },
            title = { Text("Filtros", color = Color.Black) },
            text = {
                Column {
                    Text("País:", fontWeight = FontWeight.Bold, color = Color.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val countries = listOf("Todos") + getCountries()
                        countries.chunked(3).forEach { chunk ->
                            Column {
                                chunk.forEach { country ->
                                    TextButton(
                                        onClick = {
                                            selectedCountryFilter = country
                                            showFilterMenu = false
                                        }
                                    ) {
                                        Text(
                                            country,
                                            color = if (selectedCountryFilter == country) Color.Red else Color.Blue
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Género:", fontWeight = FontWeight.Bold, color = Color.Black)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val genres = listOf("Todos") + getGenres()
                        genres.chunked(3).forEach { chunk ->
                            Column {
                                chunk.forEach { genre ->
                                    TextButton(
                                        onClick = {
                                            selectedGenreFilter = genre
                                            showFilterMenu = false
                                        }
                                    ) {
                                        Text(
                                            genre,
                                            color = if (selectedGenreFilter == genre) Color.Red else Color.Blue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedCountryFilter = "Todos"
                        selectedGenreFilter = "Todos"
                        showFilterMenu = false
                    }
                ) {
                    Text("Limpiar filtros")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterMenu = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}