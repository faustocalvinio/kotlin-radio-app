package com.example.radioapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun ExoPlayerView(context: Context, url: String, isPlaying: Boolean) {
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    val currentIsPlaying by rememberUpdatedState(isPlaying)

    DisposableEffect(url) {
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = currentIsPlaying
            Log.d("ExoPlayer", "Playing stream: $url")
        }
        player = exoPlayer

        onDispose {
            Log.d("ExoPlayer", "Releasing player")
            exoPlayer.release()
        }
    }

    LaunchedEffect(currentIsPlaying) {
        player?.playWhenReady = currentIsPlaying

        // Control del servicio para mantener la reproducción en segundo plano
        val intent = Intent(context, MusicService::class.java).apply {
            action = if (currentIsPlaying) "PLAY" else "PAUSE"
        }
        context.startService(intent)
    }
}
