package com.boni.stemflow.core.testing.fakes

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.NetworkDataSource
import java.io.IOException

class FakeNetworkDataSource : NetworkDataSource {
    var searchPages: Map<Pair<String, Int>, List<Track>> = emptyMap()
    var lookupAlbums: Map<Long, Album> = emptyMap()
    var throwOnNext: Throwable? = null

    override suspend fun search(term: String, limit: Int, offset: Int): List<Track> {
        throwOnNext?.let { throwOnNext = null; throw it }
        val page = offset / limit
        return searchPages[term to page] ?: emptyList()
    }

    override suspend fun lookupAlbum(collectionId: Long): Album {
        throwOnNext?.let { throwOnNext = null; throw it }
        return lookupAlbums[collectionId]
            ?: throw IOException("no fake album fixture for $collectionId")
    }
}
