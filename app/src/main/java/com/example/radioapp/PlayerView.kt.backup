package com.example.radioapp

import android.util.Log
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import android.content.Context

@Composable
fun ExoPlayerView(context: Context, url: String, isPlaying: Boolean) {
    var player by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(url) {
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
            Log.d("ExoPlayer", "Playing stream: $url")
        }
        player = exoPlayer

        onDispose {
            Log.d("ExoPlayer", "Releasing player")
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            player?.play()
        } else {
            player?.pause()
        }
    }

}
