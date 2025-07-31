package com.example.radioapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MediaService : Service() {
    
    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val binder = MediaBinder()
    private var currentUrl: String? = null
    private var currentStationName: String? = null
    
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "RadioAppChannel"
        
        const val ACTION_PLAY = "com.example.radioapp.PLAY"
        const val ACTION_PAUSE = "com.example.radioapp.PAUSE"
        const val ACTION_STOP = "com.example.radioapp.STOP"
    }
    
    inner class MediaBinder : Binder() {
        fun getService(): MediaService = this@MediaService
    }
    
    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        createNotificationChannel()
        setupMediaSession()
    }
    
    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updateNotification()
                    updateMediaSession()
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    updateNotification()
                    updateMediaSession()
                }
            })
        }
    }
    
    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "RadioAppMediaSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    exoPlayer?.play()
                }
                
                override fun onPause() {
                    exoPlayer?.pause()
                }
                
                override fun onStop() {
                    stopPlayback()
                }
            })
            
            isActive = true
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Radio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for radio playback"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun playRadio(url: String, stationName: String) {
        currentUrl = url
        currentStationName = stationName
        
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
        
        updateMediaSession()
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    fun pausePlayback() {
        exoPlayer?.pause()
    }
    
    fun resumePlayback() {
        exoPlayer?.play()
    }
    
    fun stopPlayback() {
        exoPlayer?.stop()
        stopForeground(true)
        stopSelf()
    }
    
    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }
    
    private fun updateMediaSession() {
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentStationName ?: "Radio")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Radio Stream")
                .build()
        )
        
        val state = if (exoPlayer?.isPlaying == true) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }
        
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_STOP
                )
                .build()
        )
    }
    
    private fun createNotification(): Notification {
        val playPauseIntent = if (exoPlayer?.isPlaying == true) {
            Intent(this, MediaService::class.java).apply { action = ACTION_PAUSE }
        } else {
            Intent(this, MediaService::class.java).apply { action = ACTION_PLAY }
        }
        
        val stopIntent = Intent(this, MediaService::class.java).apply { action = ACTION_STOP }
        
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val playPauseIcon = if (exoPlayer?.isPlaying == true) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        
        val playPauseText = if (exoPlayer?.isPlaying == true) "Pausar" else "Reproducir"
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentStationName ?: "Radio")
            .setContentText("Radio en vivo")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(mainPendingIntent)
            .setDeleteIntent(stopPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseIcon, playPauseText, playPausePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Parar", stopPendingIntent)
            .setStyle(MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0, 1))
            .setOngoing(exoPlayer?.isPlaying == true)
            .build()
    }
    
    private fun updateNotification() {
        if (currentUrl != null) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> resumePlayback()
            ACTION_PAUSE -> pausePlayback()
            ACTION_STOP -> stopPlayback()
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        mediaSession?.release()
    }
}