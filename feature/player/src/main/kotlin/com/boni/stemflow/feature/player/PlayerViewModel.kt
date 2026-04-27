package com.boni.stemflow.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boni.stemflow.core.common.di.ApplicationScope
import com.boni.stemflow.core.domain.repository.TrackRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = PlayerViewModel.Factory::class)
class PlayerViewModel @AssistedInject constructor(
    @param:Assisted private val trackId: Long,
    private val trackRepository: TrackRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val state: StateFlow<PlayerUiState> = _state
        .onStart { loadTrack() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerUiState.Loading,
        )

    private suspend fun loadTrack() {
        _state.value = PlayerUiState.Loading
        val track = runCatching { trackRepository.getTrack(trackId) }.getOrNull()
        _state.value = if (track != null) {
            PlayerUiState.Ready(track)
        } else {
            PlayerUiState.Error("Couldn't load track")
        }
    }

    fun onTrackPlayed(playedTrackId: Long) {
        applicationScope.launch {
            delay(500.milliseconds)
            trackRepository.markPlayed(playedTrackId)
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadTrack()
        }
    }

    fun onPlaybackRetry() {
        _state.update {
            it.updatePlayback { playback ->
                playback.copy(
                    isPlaying = true,
                    isBuffering = true,
                    errorMessage = null,
                )
            }
        }
    }

    fun onPlaybackBufferingChanged(isBuffering: Boolean) {
        _state.update {
            it.updatePlayback { playback -> playback.copy(isBuffering = isBuffering) }
        }
    }

    fun onPlaybackDurationChanged(durationMs: Long) {
        if (durationMs <= 0L) return

        _state.update {
            it.updatePlayback { playback -> playback.copy(durationMs = durationMs) }
        }
    }

    fun onPlaybackPositionChanged(positionMs: Long) {
        _state.update {
            it.updatePlayback { playback -> playback.copy(positionMs = positionMs) }
        }
    }

    fun onPlaybackError(message: String?) {
        _state.update {
            it.updatePlayback { playback ->
                playback.copy(
                    isPlaying = false,
                    isBuffering = false,
                    errorMessage = message,
                )
            }
        }
    }

    fun togglePlayPause() {
        _state.update {
            it.updatePlayback { playback ->
                playback.copy(
                    isPlaying = !playback.isPlaying,
                    errorMessage = null,
                )
            }
        }
    }

    fun toggleRepeat() {
        _state.update {
            it.updatePlayback { playback ->
                playback.copy(repeatEnabled = !playback.repeatEnabled)
            }
        }
    }

    fun seekBy(deltaMs: Long) {
        _state.update {
            it.updatePlayback { playback ->
                val upperBound = if (playback.durationMs > 0L) playback.durationMs else Long.MAX_VALUE
                playback.copy(seekTarget = (playback.positionMs + deltaMs).coerceIn(0L, upperBound))
            }
        }
    }

    fun seekTo(positionMs: Long) {
        _state.update {
            it.updatePlayback { playback ->
                val upperBound = if (playback.durationMs > 0L) playback.durationMs else Long.MAX_VALUE
                playback.copy(seekTarget = positionMs.coerceIn(0L, upperBound))
            }
        }
    }

    fun consumeSeek() {
        _state.update {
            it.updatePlayback { playback -> playback.copy(seekTarget = null) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(trackId: Long): PlayerViewModel
    }
}
