package com.boni.stemflow.core.data.local

import androidx.room.withTransaction
import com.boni.stemflow.core.data.mapper.toDomain
import com.boni.stemflow.core.data.mapper.toEntity
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.database.dao.AlbumDao
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class LocalITunesLocalDataSource @Inject constructor(
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val db: StemflowDatabase,
) : ITunesLocalDataSource {

    override fun observeAlbum(collectionId: Long): Flow<Album?> =
        combine(
            albumDao.observeById(collectionId),
            trackDao.observeTracksForCollection(collectionId),
        ) { albumEntity, trackEntities ->
            albumEntity?.toDomain(trackEntities.map { it.toDomain() })
        }

    override suspend fun upsertAlbum(album: Album) {
        db.withTransaction {
            albumDao.upsert(album.toEntity())
            trackDao.upsertAll(album.tracks.map { it.toEntity() })
        }
    }

    override fun observeRecentlyPlayed(): Flow<List<Track>> =
        recentlyPlayedDao.observeRecent().map { list -> list.map { it.toDomain() } }

    override suspend fun getTrack(trackId: Long): Track? =
        trackDao.getById(trackId)?.toDomain()

    override suspend fun upsertTrack(track: Track) {
        trackDao.upsertAll(listOf(track.toEntity()))
    }

    override suspend fun markTrackPlayed(trackId: Long, nowMs: Long) {
        recentlyPlayedDao.markPlayed(trackId = trackId, nowMs = nowMs)
    }
}
