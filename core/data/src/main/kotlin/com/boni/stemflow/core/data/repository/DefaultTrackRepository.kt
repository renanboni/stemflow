package com.boni.stemflow.core.data.repository

import com.boni.stemflow.core.common.time.Clock
import com.boni.stemflow.core.data.mapper.toDomain
import com.boni.stemflow.core.data.mapper.toEntity
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.TrackRepository
import com.boni.stemflow.core.network.NetworkDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class DefaultTrackRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val network: NetworkDataSource,
    private val clock: Clock,
) : TrackRepository {

    override fun getRecentlyPlayed(): Flow<List<Track>> =
        recentlyPlayedDao.observeRecent().map { list -> list.map { it.toDomain() } }

    override suspend fun getTrack(trackId: Long): Track? {
        trackDao.getById(trackId)?.let { return it.toDomain() }
        val fetched = network.getTrack(trackId) ?: return null
        trackDao.upsertAll(listOf(fetched.toEntity()))
        return fetched
    }

    override suspend fun markPlayed(trackId: Long) {
        recentlyPlayedDao.markPlayed(trackId = trackId, nowMs = clock.nowMillis())
    }
}
