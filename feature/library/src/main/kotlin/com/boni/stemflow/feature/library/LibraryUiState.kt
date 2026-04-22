package com.boni.stemflow.feature.library

import com.boni.stemflow.core.domain.model.Track

data class LibraryUiState(
    val query: String = "",
    val recentlyPlayed: List<Track> = emptyList(),
    val isOffline: Boolean = false,
)
