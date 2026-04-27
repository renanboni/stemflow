package com.boni.stemflow.core.data.repository

import androidx.paging.testing.asSnapshot
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.ITunesRemoteDataSource
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSearchRepositoryTest {

    private val network = TestNetworkDataSource()
    private val repo = DefaultSearchRepository(network)

    @Test
    fun `searchPaging emits network results for query`() = runTest {
        network.searchResults = TrackFixtures.tracks(count = 25)

        val snapshot = repo.searchPaging("queen").asSnapshot()

        assertEquals((1L..25L).toList(), snapshot.map { it.trackId })
        assertEquals(listOf("queen"), network.searchCalls.map { it.term }.distinct())
        assertEquals(listOf(0), network.searchCalls.map { it.offset }.distinct())
    }

    @Test
    fun `searchPaging returns empty list for blank query without network call`() = runTest {
        network.searchResults = TrackFixtures.tracks(count = 25)

        val snapshot = repo.searchPaging(" ").asSnapshot()

        assertEquals(emptyList<Track>(), snapshot)
        assertEquals(emptyList<SearchCall>(), network.searchCalls)
    }

    private data class SearchCall(
        val term: String,
        val limit: Int,
        val offset: Int,
    )

    private class TestNetworkDataSource : ITunesRemoteDataSource {
        var searchResults: List<Track> = emptyList()
        val searchCalls = mutableListOf<SearchCall>()

        override suspend fun search(term: String, limit: Int, offset: Int): List<Track> {
            searchCalls += SearchCall(term = term, limit = limit, offset = offset)
            return searchResults.take(limit)
        }

        override suspend fun getAlbum(collectionId: Long): Album =
            throw UnsupportedOperationException("Not used")

        override suspend fun getTrack(trackId: Long): Track? = null
    }
}
