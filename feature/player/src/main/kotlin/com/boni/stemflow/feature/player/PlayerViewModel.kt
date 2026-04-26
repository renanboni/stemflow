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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = PlayerViewModel.Factory::class)
class PlayerViewModel @AssistedInject constructor(
    @param:Assisted private val trackId: Long,
    private val trackRepository: TrackRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    val state: StateFlow<TrackLoadState> = flow {
        emit(TrackLoadState.Loading)
        val track = runCatching { trackRepository.getTrack(trackId) }.getOrNull()
        emit(
            if (track != null) TrackLoadState.Ready(track)
            else TrackLoadState.Error("Couldn't load track"),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = TrackLoadState.Loading
    )

    fun onTrackPlayed(playedTrackId: Long) {
        applicationScope.launch {
            delay(500.milliseconds)
            trackRepository.markPlayed(playedTrackId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(trackId: Long): PlayerViewModel
    }
}
