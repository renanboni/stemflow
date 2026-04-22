package com.boni.stemflow.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boni.stemflow.core.data.paging.SEARCH_PAGE_SIZE
import com.boni.stemflow.core.data.paging.SearchRemoteMediator
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.testing.fakes.FakeNetworkDataSource
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class OfflineFirstSearchRepositoryTest {

    private lateinit var db: StemflowDatabase
    private lateinit var fakeNetwork: FakeNetworkDataSource

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StemflowDatabase::class.java,
        ).allowMainThreadQueries().build()
        fakeNetwork = FakeNetworkDataSource()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun emptyState() = PagingState<Int, com.boni.stemflow.core.database.entity.TrackEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = SEARCH_PAGE_SIZE),
        leadingPlaceholderCount = 0,
    )

    @Test
    fun remoteMediator_refresh_writesTracksAndKeys() = runTest {
        fakeNetwork.searchPages = mapOf(
            ("jazz" to 0) to TrackFixtures.tracks(SEARCH_PAGE_SIZE),
        )
        val mediator = SearchRemoteMediator("jazz", fakeNetwork, db)

        val result = mediator.load(LoadType.REFRESH, emptyState())

        assertEquals(false, (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertEquals(SEARCH_PAGE_SIZE, db.searchResultDao().countFor("jazz"))
        assertEquals(1, db.remoteKeysDao().keysForQuery("jazz")!!.nextKey)
    }

    @Test
    fun remoteMediator_refresh_shortPage_marksEndOfPagination() = runTest {
        fakeNetwork.searchPages = mapOf(
            ("jazz" to 0) to TrackFixtures.tracks(3),
        )
        val mediator = SearchRemoteMediator("jazz", fakeNetwork, db)

        val result = mediator.load(LoadType.REFRESH, emptyState()) as RemoteMediator.MediatorResult.Success

        assertEquals(true, result.endOfPaginationReached)
    }

    @Test
    fun pagingSource_returnsCachedTracksInOrder() = runTest {
        fakeNetwork.searchPages = mapOf(
            ("jazz" to 0) to TrackFixtures.tracks(SEARCH_PAGE_SIZE),
        )
        SearchRemoteMediator("jazz", fakeNetwork, db).load(LoadType.REFRESH, emptyState())

        val source = db.searchResultDao().pagingSourceFor("jazz")
        val load = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        ) as PagingSource.LoadResult.Page

        assertEquals((1L..10L).toList(), load.data.map { it.trackId })
    }
}
