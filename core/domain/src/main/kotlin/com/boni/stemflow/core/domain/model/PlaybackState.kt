package com.boni.stemflow.core.domain.model

data class PlaybackState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val error: PlaybackError? = null,
)

sealed interface PlaybackError {
    data object Network : PlaybackError
    data object Decoder : PlaybackError
    data class Unknown(val message: String) : PlaybackError
}
