package com.boni.stemflow.core.network

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.api.ITunesApiService
import com.boni.stemflow.core.network.mapper.toAlbumOrNull
import com.boni.stemflow.core.network.mapper.toDomainOrNull
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ITunesNetworkDataSource @Inject constructor(
    private val api: ITunesApiService,
) : NetworkDataSource {

    override suspend fun search(term: String, limit: Int, offset: Int): List<Track> =
        api.search(term = term, limit = limit, offset = offset)
            .results
            .mapNotNull { it.toDomainOrNull() }

    override suspend fun lookupAlbum(collectionId: Long): Album =
        api.lookup(id = collectionId).toAlbumOrNull()
            ?: throw IOException("Album not found or empty response: $collectionId")
}
