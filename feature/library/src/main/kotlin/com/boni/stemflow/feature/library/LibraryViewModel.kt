package com.boni.stemflow.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.boni.stemflow.core.common.network.ConnectivityObserver
import com.boni.stemflow.core.common.network.ConnectivityState
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.SearchRepository
import com.boni.stemflow.core.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    trackRepository: TrackRepository,
    connectivity: ConnectivityObserver,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val pagedTracks: Flow<PagingData<Track>> = query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(PagingData.empty())
            else searchRepository.searchPaging(q)
        }
        .cachedIn(viewModelScope)

    val uiState: StateFlow<LibraryUiState> = combine(
        query,
        trackRepository.getRecentlyPlayed(),
        connectivity.state.map { it == ConnectivityState.Disconnected },
    ) { q, recent, offline ->
        LibraryUiState(query = q, recentlyPlayed = recent, isOffline = offline)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    fun onQueryChange(q: String) {
        query.value = q
    }
}
