package com.boni.stemflow.core.data.repository

import androidx.room.withTransaction
import com.boni.stemflow.core.common.flow.cacheFirstFlow
import com.boni.stemflow.core.data.mapper.toDomain
import com.boni.stemflow.core.data.mapper.toEntity
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.database.dao.AlbumDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.repository.AlbumRepository
import com.boni.stemflow.core.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAlbumRepository @Inject constructor(
    private val network: NetworkDataSource,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val db: StemflowDatabase,
) : AlbumRepository {

    override fun getAlbum(collectionId: Long): Flow<Album?> {
        val combined: Flow<Album?> = combine(
            albumDao.observeById(collectionId),
            trackDao.observeTracksForCollection(collectionId),
        ) { albumEntity, trackEntities ->
            albumEntity?.toDomain(trackEntities.map { it.toDomain() })
        }

        return cacheFirstFlow(
            source = combined,
            refresh = { refreshAlbum(collectionId) },
        )
    }

    override suspend fun refreshAlbum(collectionId: Long): Result<Unit> = runCatching {
        val album = network.lookupAlbum(collectionId)
        db.withTransaction {
            albumDao.upsert(album.toEntity())
            trackDao.upsertAll(album.tracks.map { it.toEntity() })
        }
    }
}
