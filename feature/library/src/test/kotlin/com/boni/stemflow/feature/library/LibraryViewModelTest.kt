package com.boni.stemflow.feature.library

import androidx.paging.PagingData
import app.cash.turbine.test
import com.boni.stemflow.core.common.network.ConnectivityObserver
import com.boni.stemflow.core.common.network.ConnectivityState
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.SearchRepository
import com.boni.stemflow.core.domain.repository.TrackRepository
import com.boni.stemflow.core.testing.coroutines.MainDispatcherRule
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LibraryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val searchQueries = mutableListOf<String>()
    private val searchRepo = object : SearchRepository {
        override fun searchPaging(query: String): Flow<PagingData<Track>> {
            searchQueries += query
            return flowOf(PagingData.empty())
        }
    }

    private val recent = MutableStateFlow<List<Track>>(emptyList())
    private val trackRepo = object : TrackRepository {
        override fun getRecentlyPlayed(): Flow<List<Track>> = recent
        override suspend fun getTrack(trackId: Long): Track? = null
        override suspend fun markPlayed(trackId: Long) = Unit
    }

    private val connState = MutableStateFlow(ConnectivityState.Connected)
    private val connectivity = object : ConnectivityObserver {
        override val state: Flow<ConnectivityState> = connState
    }

    @Test
    fun `query propagates to uiState immediately`() = runTest {
        val vm = LibraryViewModel(searchRepo, trackRepo, connectivity)
        vm.uiState.test {
            assertEquals("", awaitItem().query)
            vm.onQueryChange("jaz")
            assertEquals("jaz", awaitItem().query)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `debounce collapses rapid queries into a single search call`() = runTest {
        val vm = LibraryViewModel(searchRepo, trackRepo, connectivity)
        vm.pagedTracks.test {
            awaitItem()
            vm.onQueryChange("j")
            vm.onQueryChange("ja")
            vm.onQueryChange("jaz")
            advanceTimeBy(299)
            assertTrue(searchQueries.none { it.isNotBlank() })
            advanceTimeBy(2)
            awaitItem()
            assertEquals(listOf("jaz"), searchQueries.filter { it.isNotBlank() })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `offline flag reflects connectivity state`() = runTest {
        val vm = LibraryViewModel(searchRepo, trackRepo, connectivity)
        vm.uiState.test {
            assertFalse(awaitItem().isOffline)
            connState.value = ConnectivityState.Disconnected
            assertTrue(awaitItem().isOffline)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `recently played propagates to uiState`() = runTest {
        val vm = LibraryViewModel(searchRepo, trackRepo, connectivity)
        vm.uiState.test {
            assertEquals(0, awaitItem().recentlyPlayed.size)
            recent.value = TrackFixtures.tracks(3)
            assertEquals(3, awaitItem().recentlyPlayed.size)
            cancelAndConsumeRemainingEvents()
        }
    }
}
