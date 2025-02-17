package com.example.radioapp

import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicService : Service() {

    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private val TAG = "MusicService"

    override fun onCreate() {
        super.onCreate()
        player = PlayerHolder.getPlayer(this)
        createNotificationChannel()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            "PLAY" -> playMusic()
            "PAUSE" -> pauseMusic()
            "STOP" -> stopSelf()
        }
        return START_STICKY
    }

    private fun playMusic() {
        if (!isPlaying) {
            player.setMediaItem(MediaItem.fromUri("https://your-stream-url.com"))
            player.prepare()
            player.play()
            isPlaying = true
            showNotification()
            Log.d(TAG, "Music started")
        }
    }

    private fun pauseMusic() {
        if (isPlaying) {
            player.pause()
            isPlaying = false
            showNotification()
            Log.d(TAG, "Music paused")
        }
    }

    private fun showNotification() {
        val playIntent = Intent(this, MusicService::class.java).apply { action = "PLAY" }
        val pauseIntent = Intent(this, MusicService::class.java).apply { action = "PAUSE" }
        val stopIntent = Intent(this, MusicService::class.java).apply { action = "STOP" }

        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stopPendingIntent = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Radio App")
            .setContentText(if (isPlaying) "Reproduciendo" else "Pausado")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                if (isPlaying) R.drawable.pause_circle_24px else R.drawable.play_circle_24px,
                if (isPlaying) "Pausar" else "Reproducir",
                if (isPlaying) pausePendingIntent else playPendingIntent
            )
            .addAction(R.drawable.stop_circle_24px, "Detener", stopPendingIntent)
            .setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "music_channel",
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        PlayerHolder.releasePlayer()
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
