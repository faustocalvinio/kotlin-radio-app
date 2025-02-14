package com.example.radioapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import android.graphics.Color.BLACK
import androidx.compose.ui.unit.TextUnit
import com.example.radioapp.ui.theme.RadioAPPTheme

import androidx.annotation.ColorInt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    private val radioStations = mapOf(
        "Galaxxy France" to "https://eu8.fastcast4u.com/proxy/rockfmgm?mp=/1",
        "Los 40" to "https://edge02.radiohdvivo.com/stream/los40",
        "Aspen" to "https://27413.live.streamtheworld.com/ASPEN.mp3",
        "VORTERIX" to "https://ice2.edge-apps.net/radio1_high-20057.audio",
        "Boing" to "https://streaming.redboing.com/radio/8000/radio.aac",
        "Del Siglo" to "https://stream.lt8.com.ar:8443/delsiglo995.mp3",
        "88.7" to "https://streaming.redboing.com/radio/8010/radio.aac",
        "Crystal FM" to "https://radio02.ferozo.com/proxy/ra02001330?mp=/stream?ver%3D468915"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()




        setContent {
            RadioAPPTheme {
                val context = LocalContext.current
                var selectedUrl by remember { mutableStateOf<String?>(null) }
                var selectedRadio by remember { mutableStateOf<String?>(null) }
                var isPlaying by remember { mutableStateOf(true) }

                Scaffold(modifier = Modifier.fillMaxSize().background(Color.Black)) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxHeight().background(Color.Black), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        radioStations.forEach { (name, url) ->
                            Button(modifier = Modifier.fillMaxWidth().height(78.dp).padding(end = 20.dp),onClick = {
                                selectedUrl = url
                                selectedRadio = name
                                isPlaying = true
                                Log.d("MainActivity", "Selected URL: $url")
                            }) {
                                Text(text = name,style = MaterialTheme.typography.titleLarge)
                            }
                        }

                        selectedRadio?.let { radioName ->
                            Text(
                                text = "Reproduciendo: $radioName",
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }

                        selectedUrl?.let { url ->
                            ExoPlayerView(context = context, url = url, isPlaying = isPlaying)
                        }

                        Button(
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = { isPlaying = !isPlaying },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) Color.Red else Color.Green // Rojo si está reproduciendo, verde si está pausado
                            )

                        ) {
                            Text(text = if (isPlaying) "Pausar" else "Reproducir" , modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }


    }
}
