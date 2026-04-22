package com.boni.stemflow.core.database.dao

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boni.stemflow.core.database.entity.SearchResultEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchResultDaoTest : BaseDaoTest() {

    @Test
    fun insertAllAndPagingSourceReturnInPositionOrder() = runTest {
        db.trackDao().upsertAll((1L..3L).map { trackEntity(it, collectionId = 1L) })
        db.searchResultDao().insertAll(
            listOf(
                SearchResultEntity("jazz", position = 0, pageIndex = 0, trackId = 1L),
                SearchResultEntity("jazz", position = 1, pageIndex = 0, trackId = 2L),
                SearchResultEntity("jazz", position = 2, pageIndex = 0, trackId = 3L),
            ),
        )
        val result = db.searchResultDao().pagingSourceFor("jazz").load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        ) as PagingSource.LoadResult.Page
        assertEquals(listOf(1L, 2L, 3L), result.data.map { it.trackId })
    }

    @Test
    fun clearForQueryRemovesOnlyMatchingQuery() = runTest {
        db.trackDao().upsertAll(listOf(trackEntity(1L), trackEntity(2L)))
        db.searchResultDao().insertAll(
            listOf(
                SearchResultEntity("a", position = 0, pageIndex = 0, trackId = 1L),
                SearchResultEntity("b", position = 0, pageIndex = 0, trackId = 2L),
            ),
        )
        db.searchResultDao().clearForQuery("a")
        assertEquals(0, db.searchResultDao().countFor("a"))
        assertEquals(1, db.searchResultDao().countFor("b"))
    }

    @Test
    fun maxPositionForReturnsNullOnEmpty() = runTest {
        assertNull(db.searchResultDao().maxPositionFor("missing"))
    }
}
