package com.boni.stemflow.core.domain.player

import com.boni.stemflow.core.domain.model.PlaybackState
import com.boni.stemflow.core.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface StemflowPlayer {
    val state: StateFlow<PlaybackState>
    fun play(track: Track)
    fun pause()
    fun seekTo(positionMs: Long)
    fun skipForward()
    fun skipBackward()
    fun release()
}
