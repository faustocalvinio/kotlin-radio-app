package com.example.radioapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.example.radioapp.ui.theme.RadioAPPTheme

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

@OptIn(UnstableApi::class)
@Composable
fun RadioAppScreen() {
    val context = LocalContext.current
    val radioStations = mapOf(
        "Galaxxy France" to "https://eu8.fastcast4u.com/proxy/rockfmgm?mp=/1",
        "Los 40" to "https://edge02.radiohdvivo.com/stream/los40",
        "Aspen" to "https://27413.live.streamtheworld.com/ASPEN.mp3",
        "VORTERIX" to "https://ice2.edge-apps.net/radio1_high-20057.audio",
        "Boing" to "https://streaming.redboing.com/radio/8000/radio.aac",
        "Del Siglo" to "https://stream.lt8.com.ar:8443/delsiglo995.mp3",
        "88.7" to "https://streaming.redboing.com/radio/8010/radio.aac",
        "Crystal FM" to "https://radio02.ferozo.com/proxy/ra02001330?mp=/stream?ver%3D468915"
    )
    var selectedUrl by remember { mutableStateOf<String?>(null) }
    var selectedRadio by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            radioStations.forEach { (name, url) ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedUrl = url
                        selectedRadio = name
                        val intent = Intent(context, PlayerService::class.java).apply {
                            action = PlayerNotificationManager.ACTION_PLAY
                            putExtra("url", url)
                        }
                        context.startService(intent)
                    }
                ) {
                    Text(text = name, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                }
            }

            selectedRadio?.let { radioName ->
                Text(
                    text = "Reproduciendo: $radioName",
                    modifier = Modifier.padding(top = 16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
