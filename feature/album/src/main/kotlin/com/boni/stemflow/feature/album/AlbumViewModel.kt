package com.boni.stemflow.feature.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.repository.AlbumRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = AlbumViewModel.Factory::class)
class AlbumViewModel @AssistedInject constructor(
    @Assisted private val collectionId: Long,
    albumRepository: AlbumRepository,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val state: StateFlow<AlbumLoadState> = albumRepository
        .getAlbum(collectionId)
        .map { album ->
            if (album == null) AlbumLoadState.Loading
            else AlbumLoadState.Ready(album)
        }
        .onStart { emit(AlbumLoadState.Loading) }
        .retryWhen { cause, _ ->
            emit(AlbumLoadState.Error(cause.message ?: "Couldn't load album"))
            retryRequests.first()
            true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AlbumLoadState.Loading,
        )

    fun retry() {
        retryRequests.tryEmit(Unit)
    }

    @AssistedFactory
    interface Factory {
        fun create(collectionId: Long): AlbumViewModel
    }
}
