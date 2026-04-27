package com.boni.stemflow.core.data.repository

import com.boni.stemflow.core.common.time.Clock
import com.boni.stemflow.core.data.local.ITunesLocalDataSource
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.TrackRepository
import com.boni.stemflow.core.network.ITunesRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DefaultTrackRepository @Inject constructor(
    private val local: ITunesLocalDataSource,
    private val network: ITunesRemoteDataSource,
    private val clock: Clock,
) : TrackRepository {

    override fun getRecentlyPlayed(): Flow<List<Track>> =
        local.observeRecentlyPlayed()

    override suspend fun getTrack(trackId: Long): Track? {
        local.getTrack(trackId)?.let { return it }
        val fetched = network.getTrack(trackId) ?: return null
        local.upsertTrack(fetched)
        return fetched
    }

    override suspend fun markPlayed(trackId: Long) {
        local.markTrackPlayed(trackId = trackId, nowMs = clock.nowMillis())
    }
}
