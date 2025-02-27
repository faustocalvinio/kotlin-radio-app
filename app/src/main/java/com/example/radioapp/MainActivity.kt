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
                .verticalScroll(
                    rememberScrollState()
                ), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            selectedRadio?.let { radioName ->
                Text(
                    text = "Reproduciendo: $radioName",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
            if (startedPlaying) {
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(78.dp)
                        .padding(end = 20.dp),
                    onClick = { isPlaying = !isPlaying },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color.Red else Color.Green
                    )
                ) {
                    Text(
                        text = if (isPlaying) "Pausar" else "Reproducir",

                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            radioStations.forEach { (name, url) ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                        .padding(end = 20.dp),
                    onClick = {
                        selectedUrl = url
                        selectedRadio = name
                        isPlaying = true
                        startedPlaying = true
                        Log.d("MainActivity", "Selected URL: $url")
                    }) {
                    Text(text = name, style = MaterialTheme.typography.titleLarge)
                }
            }



            selectedUrl?.let { url ->
                ExoPlayerView(context = context, url = url, isPlaying = isPlaying)
            }


        }
    }
}