package com.example.radioapp

import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        setContent { RadioAppScreen() }
    }
}

@Composable
fun RadioAppScreen() {
    val radioStations = mapOf(
        "Galaxxy France" to "https://eu8.fastcast4u.com/proxy/rockfmgm?mp=/1",
        "Los 40" to "https://edge02.radiohdvivo.com/stream/los40"
    )
    val context = LocalContext.current
    var selectedUrl by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(true) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        radioStations.forEach { (name, url) ->
            Button(onClick = {
                selectedUrl = url
                isPlaying = true
            }) {
                Text(text = name)
            }
        }
        selectedUrl?.let { url ->
            ExoPlayerView(context, url, isPlaying)
        }
    }
}

@Composable
fun ExoPlayerView(context: Context, url: String, isPlaying: Boolean) {
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    val currentIsPlaying by rememberUpdatedState(isPlaying)

    DisposableEffect(url) {
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = currentIsPlaying
        }
        player = exoPlayer

        onDispose { exoPlayer.release() }
    }

    LaunchedEffect(currentIsPlaying) {
        player?.playWhenReady = currentIsPlaying
        context.startService(Intent(context, MusicService::class.java).apply {
            action = if (currentIsPlaying) "PLAY" else "PAUSE"
        })
    }
}

class MusicService : Service() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "PLAY" -> player.play()
            "PAUSE" -> player.pause()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
