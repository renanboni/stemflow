package com.boni.stemflow.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.boni.stemflow.core.domain.model.Track

@Stable
class PlayerPlaybackState {

    var isPlaying by mutableStateOf(false)
        private set

    var positionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(0L)
        private set

    var seekTarget by mutableStateOf<Long?>(null)
        private set

    var isBuffering by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var repeatEnabled by mutableStateOf(false)
        private set

    fun onTrackLoaded(track: Track?) {
        positionMs = 0L
        durationMs = track?.trackTimeMillis ?: 0L
        isPlaying = track != null
        seekTarget = null
        isBuffering = false
        error = null
    }

    fun onIsPlayingChanged(value: Boolean) {
        isPlaying = value
    }

    fun onPositionChanged(value: Long) {
        positionMs = value
    }

    fun onDurationChanged(value: Long) {
        if (value > 0) durationMs = value
    }

    fun onBufferingChanged(value: Boolean) {
        isBuffering = value
    }

    fun onError(message: String?) {
        error = message
    }

    fun consumeSeek() {
        seekTarget = null
    }

    fun togglePlayPause() {
        isPlaying = !isPlaying
    }

    fun toggleRepeat() {
        repeatEnabled = !repeatEnabled
    }

    fun seekBy(deltaMs: Long) {
        val upperBound = if (durationMs > 0) durationMs else Long.MAX_VALUE
        seekTarget = (positionMs + deltaMs).coerceIn(0L, upperBound)
    }

    fun seekTo(positionMs: Long) {
        val upperBound = if (durationMs > 0) durationMs else Long.MAX_VALUE
        seekTarget = positionMs.coerceIn(0L, upperBound)
    }
}

@Composable
fun rememberPlayerPlaybackState(): PlayerPlaybackState = remember { PlayerPlaybackState() }
