package com.boni.stemflow.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackDaoTest : BaseDaoTest() {

    @Test
    fun upsertAll_insertsNewTracks() = runTest {
        val dao = db.trackDao()
        dao.upsertAll(listOf(trackEntity(1L), trackEntity(2L)))
        assertEquals(2, dao.getAllByIds(listOf(1L, 2L)).size)
    }

    @Test
    fun observeById_emitsNullThenTrack() = runTest {
        val dao = db.trackDao()
        dao.observeById(1L).test {
            assertNull(awaitItem())
            dao.upsertAll(listOf(trackEntity(1L)))
            assertEquals(1L, awaitItem()!!.trackId)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun upsertAll_replacesExisting() = runTest {
        val dao = db.trackDao()
        dao.upsertAll(listOf(trackEntity(1L)))
        dao.upsertAll(listOf(trackEntity(1L).copy(name = "renamed")))
        assertEquals("renamed", dao.getAllByIds(listOf(1L)).single().name)
    }

    @Test
    fun observeTracksForCollection_returnsMembersInIdOrder() = runTest {
        val dao = db.trackDao()
        dao.upsertAll(
            listOf(
                trackEntity(3L, collectionId = 10L),
                trackEntity(1L, collectionId = 10L),
                trackEntity(2L, collectionId = 10L),
                trackEntity(99L, collectionId = 20L),
            ),
        )
        dao.observeTracksForCollection(10L).test {
            val items = awaitItem()
            assertEquals(listOf(1L, 2L, 3L), items.map { it.trackId })
            cancelAndConsumeRemainingEvents()
        }
    }
}
