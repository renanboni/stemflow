package com.boni.stemflow.core.domain.repository

import com.boni.stemflow.core.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getRecentlyPlayed(): Flow<List<Track>>
    fun getTrack(trackId: Long): Flow<Track?>
    suspend fun markPlayed(trackId: Long)
}
