package com.example.radioapp

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.session.MediaSession
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.app.NotificationCompat

import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat

@Composable
fun ExoPlayerView(context: Context, url: String, isPlaying: Boolean, songTitle: String) {
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var mediaSession by remember { mutableStateOf<MediaSession?>(null) }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    DisposableEffect(url) {
        // Initialize ExoPlayer
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
            Log.d("ExoPlayer", "Playing stream: $url")
        }
        player = exoPlayer

        // Create MediaSession
        val session = MediaSession(context, "MediaSession")
        session.isActive = true
        mediaSession = session

        // Initialize notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Music Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Display notification
        createNotification(context, songTitle, isPlaying)

        onDispose {
            Log.d("ExoPlayer", "Releasing player")
            exoPlayer.release()
            session.release()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            player?.play()
        } else {
            player?.pause()
        }
        // Update notification for play/pause button
        createNotification(context, songTitle, isPlaying)
    }
}

fun createNotification(context: Context, songTitle: String, isPlaying: Boolean) {
    val playPauseAction = if (isPlaying) {
        NotificationCompat.Action(
            0, "Pause", getPlayPauseIntent(context, action = "PAUSE")
        )
    } else {
        NotificationCompat.Action(
            0, "Play", getPlayPauseIntent(context, action = "PLAY")
        )
    }

    val notification = NotificationCompat.Builder(context, "music_channel")
        .setContentTitle("Reproduciendo: $songTitle")
        .setContentText("Estación de radio")
        .setSmallIcon(android.R.drawable.ic_media_play)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
        .addAction(playPauseAction)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    // Send notification
//    if (ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.POST_NOTIFICATIONS
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        // TODO: Consider calling
//        //    ActivityCompat#requestPermissions
//        // here to request the missing permissions, and then overriding
//        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//        //                                          int[] grantResults)
//        // to handle the case where the user grants the permission. See the documentation
//        // for ActivityCompat#requestPermissions for more details.
//        return
//    }
    NotificationManagerCompat.from(context).notify(1, notification)
}
fun getPlayPauseIntent(context: Context, action: String): PendingIntent {
    val intent = Intent(context, PlayerService::class.java).apply {
        this.action = action
    }
    return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}
