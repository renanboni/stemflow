package com.boni.stemflow.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentlyPlayedDaoTest : BaseDaoTest() {

    @Test
    fun markPlayed_insertsRow() = runTest {
        db.trackDao().upsertAll(listOf(trackEntity(1L)))
        db.recentlyPlayedDao().markPlayed(1L, nowMs = 100L)
        val recent = db.recentlyPlayedDao().observeRecent().first()
        assertEquals(listOf(1L), recent.map { it.trackId })
    }

    @Test
    fun markPlayed_dedupsByTrackIdAndBumpsTimestamp() = runTest {
        db.trackDao().upsertAll(listOf(trackEntity(1L), trackEntity(2L)))
        db.recentlyPlayedDao().markPlayed(1L, nowMs = 100L)
        db.recentlyPlayedDao().markPlayed(2L, nowMs = 200L)
        db.recentlyPlayedDao().markPlayed(1L, nowMs = 300L)

        val recent = db.recentlyPlayedDao().observeRecent().first()
        assertEquals(listOf(1L, 2L), recent.map { it.trackId })
    }

    @Test
    fun markPlayed_trimsToCap() = runTest {
        db.trackDao().upsertAll((1L..25L).map { trackEntity(it) })
        (1L..25L).forEach { id ->
            db.recentlyPlayedDao().markPlayed(id, nowMs = id * 10L)
        }
        val recent = db.recentlyPlayedDao().observeRecent().first()
        assertEquals(20, recent.size)
        assertEquals(25L, recent.first().trackId)
        assertEquals(6L, recent.last().trackId)
    }
}
