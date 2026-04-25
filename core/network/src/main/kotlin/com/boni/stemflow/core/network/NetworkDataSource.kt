package com.boni.stemflow.core.network

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track

interface NetworkDataSource {
    suspend fun search(term: String, limit: Int, offset: Int): List<Track>
    suspend fun getAlbum(collectionId: Long): Album
    suspend fun getTrack(trackId: Long): Track?
}
