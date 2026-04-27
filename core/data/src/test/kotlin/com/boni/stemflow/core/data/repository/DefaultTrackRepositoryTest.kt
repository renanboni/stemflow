package com.boni.stemflow.core.data.repository

import app.cash.turbine.test
import com.boni.stemflow.core.common.time.Clock
import com.boni.stemflow.core.data.local.ITunesLocalDataSource
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.testing.fakes.FakeITunesRemoteDataSource
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultTrackRepositoryTest {

    private lateinit var local: TestITunesLocalDataSource
    private lateinit var network: FakeITunesRemoteDataSource
    private var nowMs: Long = 42L
    private lateinit var repo: DefaultTrackRepository

    @Before
    fun setUp() {
        local = TestITunesLocalDataSource()
        network = FakeITunesRemoteDataSource()
        repo = DefaultTrackRepository(local, network, Clock { nowMs })
    }

    @Test
    fun `getRecentlyPlayed emits domain Tracks ordered by recency`() = runTest {
        local.upsertTrack(TrackFixtures.track(id = 1L))
        local.upsertTrack(TrackFixtures.track(id = 2L))
        local.markTrackPlayed(1L, nowMs = 100L)
        local.markTrackPlayed(2L, nowMs = 200L)

        repo.getRecentlyPlayed().test {
            val list = awaitItem()
            assertEquals(listOf(2L, 1L), list.map { it.trackId })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `markPlayed uses the supplied clock`() = runTest {
        nowMs = 999L
        local.upsertTrack(TrackFixtures.track(id = 1L))
        repo.markPlayed(1L)

        assertEquals(999L, local.recentlyPlayed.single().playedAt)
    }

    @Test
    fun `getTrack returns cached local track without network request`() = runTest {
        val track = TrackFixtures.track(id = 7L)
        local.upsertTrack(track)
        network.throwOnNext = AssertionError("Network should not be called")

        assertEquals(track, repo.getTrack(7L))
    }

    @Test
    fun `getTrack stores fetched network track locally`() = runTest {
        val track = TrackFixtures.track(id = 8L)
        network.tracksById = mapOf(8L to track)

        assertEquals(track, repo.getTrack(8L))
        assertEquals(track, local.tracks[8L])
    }

    @Test
    fun `getTrack returns null when network misses`() = runTest {
        assertNull(repo.getTrack(999L))
    }
}

private class TestITunesLocalDataSource : ITunesLocalDataSource {
    val tracks = mutableMapOf<Long, Track>()
    val recentlyPlayed = mutableListOf<RecentlyPlayed>()
    private val changes = MutableStateFlow(0)

    override fun observeAlbum(collectionId: Long): Flow<Album?> =
        changes.map { null }

    override suspend fun upsertAlbum(album: Album) = Unit

    override fun observeRecentlyPlayed(): Flow<List<Track>> = changes.map {
        recentlyPlayed
            .sortedByDescending { it.playedAt }
            .mapNotNull { tracks[it.trackId] }
    }

    override suspend fun getTrack(trackId: Long): Track? = tracks[trackId]

    override suspend fun upsertTrack(track: Track) {
        tracks[track.trackId] = track
        changes.value += 1
    }

    override suspend fun markTrackPlayed(trackId: Long, nowMs: Long) {
        recentlyPlayed.removeAll { it.trackId == trackId }
        recentlyPlayed += RecentlyPlayed(trackId = trackId, playedAt = nowMs)
        changes.value += 1
    }

    data class RecentlyPlayed(
        val trackId: Long,
        val playedAt: Long,
    )
}
