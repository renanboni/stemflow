package com.boni.stemflow.feature.player

import com.boni.stemflow.core.domain.model.Track

sealed interface TrackLoadState {
    data object Loading : TrackLoadState
    data class Error(val message: String) : TrackLoadState
    data class Ready(val track: Track) : TrackLoadState
}
