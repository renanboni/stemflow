package com.boni.stemflow.core.domain.repository

import com.boni.stemflow.core.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getRecentlyPlayed(): Flow<List<Track>>
    suspend fun getTrack(trackId: Long): Track?
    suspend fun markPlayed(trackId: Long)
}
