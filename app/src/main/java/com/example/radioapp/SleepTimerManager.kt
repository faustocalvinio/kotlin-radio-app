package com.example.radioapp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*

class SleepTimerManager {
    var isTimerActive by mutableStateOf(false)
        private set
    
    var timeRemaining by mutableStateOf(0L)
        private set
    
    private var timerJob: Job? = null
    
    fun startTimer(minutes: Int, onTimerEnd: () -> Unit) {
        stopTimer()
        
        val totalTime = minutes * 60 * 1000L // Convert to milliseconds
        timeRemaining = totalTime
        isTimerActive = true
        
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (timeRemaining > 0 && isActive) {
                delay(1000L) // Wait 1 second
                timeRemaining -= 1000L
            }
            
            if (isActive) {
                isTimerActive = false
                timeRemaining = 0L
                onTimerEnd()
            }
        }
    }
    
    fun stopTimer() {
        timerJob?.cancel()
        isTimerActive = false
        timeRemaining = 0L
    }
    
    fun getFormattedTimeRemaining(): String {
        val minutes = (timeRemaining / 1000) / 60
        val seconds = (timeRemaining / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}