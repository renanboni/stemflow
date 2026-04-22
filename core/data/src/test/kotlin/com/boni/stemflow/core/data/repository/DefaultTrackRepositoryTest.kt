package com.boni.stemflow.core.data.repository

import app.cash.turbine.test
import com.boni.stemflow.core.common.time.Clock
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.database.entity.RecentlyPlayedEntity
import com.boni.stemflow.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultTrackRepositoryTest {

    private lateinit var trackDao: FakeTrackDao
    private lateinit var recentDao: FakeRecentlyPlayedDao
    private var nowMs: Long = 42L
    private lateinit var repo: DefaultTrackRepository

    @Before
    fun setUp() {
        trackDao = FakeTrackDao()
        recentDao = FakeRecentlyPlayedDao(trackDao)
        repo = DefaultTrackRepository(trackDao, recentDao, Clock { nowMs })
    }

    @Test
    fun `getRecentlyPlayed emits domain Tracks ordered by recency`() = runTest {
        trackDao.upsertAll(listOf(trackEntity(1L), trackEntity(2L)))
        recentDao.markPlayed(1L, nowMs = 100L)
        recentDao.markPlayed(2L, nowMs = 200L)

        repo.getRecentlyPlayed().test {
            val list = awaitItem()
            assertEquals(listOf(2L, 1L), list.map { it.trackId })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `markPlayed uses the supplied clock`() = runTest {
        nowMs = 999L
        trackDao.upsertAll(listOf(trackEntity(1L)))
        repo.markPlayed(1L)

        assertEquals(999L, recentDao.rows.single().playedAt)
    }

    @Test
    fun `getTrack emits null when missing`() = runTest {
        repo.getTrack(999L).test {
            assertNull(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun trackEntity(id: Long) = TrackEntity(
        trackId = id,
        name = "n$id",
        artistName = "a",
        artistId = 1L,
        collectionId = 100L,
        collectionName = "c",
        artworkUrl100 = null,
        artworkUrl600 = null,
        previewUrl = null,
        trackTimeMillis = null,
        primaryGenreName = null,
        releaseDate = null,
        trackViewUrl = null,
    )
}

private class FakeTrackDao : TrackDao {
    val stored = mutableMapOf<Long, TrackEntity>()
    private val changes = MutableStateFlow(0)

    override suspend fun upsertAll(tracks: List<TrackEntity>) {
        tracks.forEach { stored[it.trackId] = it }
        changes.value += 1
    }

    override fun observeById(trackId: Long): Flow<TrackEntity?> =
        changes.map { stored[trackId] }

    override suspend fun getAllByIds(ids: List<Long>): List<TrackEntity> =
        ids.mapNotNull { stored[it] }

    override fun observeTracksForCollection(id: Long): Flow<List<TrackEntity>> =
        changes.map { stored.values.filter { it.collectionId == id }.sortedBy { it.trackId } }

    fun orderedByIds(ids: List<Long>): List<TrackEntity> =
        ids.mapNotNull { stored[it] }
}

private class FakeRecentlyPlayedDao(
    private val trackDao: FakeTrackDao,
) : RecentlyPlayedDao {
    val rows = mutableListOf<RecentlyPlayedEntity>()
    private val changes = MutableStateFlow(0)

    override suspend fun insertOrReplace(entry: RecentlyPlayedEntity) {
        rows.removeAll { it.trackId == entry.trackId }
        rows.add(entry)
        changes.value += 1
    }

    override suspend fun trimToNewest(keep: Int) {
        val sorted = rows.sortedByDescending { it.playedAt }.take(keep)
        rows.clear()
        rows.addAll(sorted)
        changes.value += 1
    }

    override fun observeRecent(): Flow<List<TrackEntity>> = changes.map {
        val ordered = rows.sortedByDescending { it.playedAt }.map { it.trackId }
        trackDao.orderedByIds(ordered)
    }
}
