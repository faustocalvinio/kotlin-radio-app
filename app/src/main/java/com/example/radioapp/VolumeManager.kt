package com.example.radioapp

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class VolumeManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    var volume by mutableStateOf(getCurrentVolume())
        private set
    
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    
    private fun getCurrentVolume(): Float {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        return currentVolume.toFloat() / maxVolume.toFloat()
    }
    
    fun setVolume(volumeLevel: Float) {
        val actualVolume = (volumeLevel * maxVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, actualVolume, 0)
        volume = volumeLevel
    }
    
    fun updateCurrentVolume() {
        volume = getCurrentVolume()
    }
}