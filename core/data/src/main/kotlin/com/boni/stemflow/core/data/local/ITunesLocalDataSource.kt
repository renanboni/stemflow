package com.boni.stemflow.core.data.local

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface ITunesLocalDataSource {
    fun observeAlbum(collectionId: Long): Flow<Album?>

    suspend fun upsertAlbum(album: Album)

    fun observeRecentlyPlayed(): Flow<List<Track>>

    suspend fun getTrack(trackId: Long): Track?

    suspend fun upsertTrack(track: Track)

    suspend fun markTrackPlayed(trackId: Long, nowMs: Long)
}
