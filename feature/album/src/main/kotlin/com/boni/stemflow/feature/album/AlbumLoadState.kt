package com.boni.stemflow.feature.album

import com.boni.stemflow.core.domain.model.Album

sealed interface AlbumLoadState {
    data object Loading : AlbumLoadState
    data class Error(val message: String) : AlbumLoadState
    data class Ready(val album: Album) : AlbumLoadState
}
