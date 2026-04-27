package com.boni.stemflow.core.data.repository

import com.boni.stemflow.core.data.local.ITunesLocalDataSource
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.repository.AlbumRepository
import com.boni.stemflow.core.network.ITunesRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAlbumRepository @Inject constructor(
    private val network: ITunesRemoteDataSource,
    private val local: ITunesLocalDataSource,
) : AlbumRepository {

    override fun getAlbum(collectionId: Long): Flow<Album?> = flow {
        val cachedAlbum = local.observeAlbum(collectionId).first()

        if (cachedAlbum != null) {
            emit(cachedAlbum)
            runCatching { refreshAlbum(collectionId) }
        } else {
            refreshAlbum(collectionId)
        }

        emitAll(local.observeAlbum(collectionId))
    }

    override suspend fun refreshAlbum(collectionId: Long) {
        local.upsertAlbum(network.getAlbum(collectionId))
    }
}
