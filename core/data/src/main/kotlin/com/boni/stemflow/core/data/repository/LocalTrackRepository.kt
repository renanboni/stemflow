package com.boni.stemflow.core.data.repository

import com.boni.stemflow.core.data.mapper.toDomain
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTrackRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val clock: () -> Long = System::currentTimeMillis,
) : TrackRepository {

    override fun getRecentlyPlayed(): Flow<List<Track>> =
        recentlyPlayedDao.observeRecent().map { list -> list.map { it.toDomain() } }

    override fun getTrack(trackId: Long): Flow<Track?> =
        trackDao.observeById(trackId).map { it?.toDomain() }

    override suspend fun markPlayed(trackId: Long) {
        recentlyPlayedDao.markPlayed(trackId = trackId, nowMs = clock())
    }
}
