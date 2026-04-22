package com.boni.stemflow.core.domain.repository

import com.boni.stemflow.core.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbum(collectionId: Long): Flow<Album?>
    suspend fun refreshAlbum(collectionId: Long): Result<Unit>
}
