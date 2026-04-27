package com.boni.stemflow.feature.player

import com.boni.stemflow.core.domain.model.Track

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Error(val message: String) : PlayerUiState
    data class Ready(
        val track: Track,
        val playback: PlaybackUiState = PlaybackUiState(
            isPlaying = true,
            durationMs = track.trackTimeMillis ?: 0L,
        ),
    ) : PlayerUiState
}

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val seekTarget: Long? = null,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null,
    val repeatEnabled: Boolean = false,
)

internal inline fun PlayerUiState.updatePlayback(
    transform: (PlaybackUiState) -> PlaybackUiState,
): PlayerUiState = when (this) {
    PlayerUiState.Loading -> this
    is PlayerUiState.Error -> this
    is PlayerUiState.Ready -> copy(playback = transform(playback))
}
