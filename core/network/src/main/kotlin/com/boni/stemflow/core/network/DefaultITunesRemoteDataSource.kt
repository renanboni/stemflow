package com.boni.stemflow.core.network

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.api.ITunesApiService
import com.boni.stemflow.core.network.mapper.toAlbumOrNull
import com.boni.stemflow.core.network.mapper.toDomainOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultITunesRemoteDataSource @Inject constructor(
    private val api: ITunesApiService,
) : ITunesRemoteDataSource {

    override suspend fun search(term: String, limit: Int, offset: Int): List<Track> =
        api.search(term = term, limit = limit, offset = offset)
            .results
            .mapNotNull { it.toDomainOrNull() }

    override suspend fun getAlbum(collectionId: Long): Album =
        api.get(id = collectionId).toAlbumOrNull()
            ?: throw RemoteDataException.NotFound("Album not found: $collectionId")

    override suspend fun getTrack(trackId: Long): Track? =
        api.get(id = trackId, entity = "song")
            .results
            .firstOrNull()
            ?.toDomainOrNull()
}
