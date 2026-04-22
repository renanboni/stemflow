package com.boni.stemflow.feature.search

import com.boni.stemflow.core.domain.model.Track

data class HomeUiState(
    val query: String = "",
    val recentlyPlayed: List<Track> = emptyList(),
    val isOffline: Boolean = false,
)
