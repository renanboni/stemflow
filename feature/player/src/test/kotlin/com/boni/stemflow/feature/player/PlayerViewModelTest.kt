package com.boni.stemflow.feature.player

import app.cash.turbine.test
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.TrackRepository
import com.boni.stemflow.core.testing.coroutines.MainDispatcherRule
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PlayerViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `state loads track when collected`() = runTest {
        val track = TrackFixtures.track(id = 10L, trackTimeMillis = 180_000L)
        val repo = TestTrackRepository(track)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.state.test {
            assertEquals(PlayerUiState.Loading, awaitItem())
            assertEquals(PlayerUiState.Ready(track), awaitItem())
            assertEquals(1, repo.loadAttempts)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `retry reloads track after load error`() = runTest {
        val track = TrackFixtures.track(id = 10L)
        val repo = TestTrackRepository(null)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.state.test {
            assertEquals(PlayerUiState.Loading, awaitItem())
            assertEquals(PlayerUiState.Error("Couldn't load track"), awaitItem())

            repo.track = track
            repo.holdNextLoad = CompletableDeferred()
            vm.retry()

            assertEquals(PlayerUiState.Loading, awaitItem())
            repo.holdNextLoad?.complete(Unit)
            assertEquals(PlayerUiState.Ready(track), awaitItem())
            assertEquals(2, repo.loadAttempts)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `playback error updates ready playback state`() = runTest {
        val track = TrackFixtures.track(id = 10L)
        val repo = TestTrackRepository(track)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.state.test {
            assertEquals(PlayerUiState.Loading, awaitItem())
            assertEquals(PlayerUiState.Ready(track), awaitItem())

            vm.onPlaybackError("No network")

            val state = awaitItem() as PlayerUiState.Ready
            assertEquals("No network", state.playback.errorMessage)
            assertFalse(state.playback.isPlaying)
            assertFalse(state.playback.isBuffering)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `playback retry clears error and shows buffering`() = runTest {
        val track = TrackFixtures.track(id = 10L)
        val repo = TestTrackRepository(track)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.state.test {
            assertEquals(PlayerUiState.Loading, awaitItem())
            assertEquals(PlayerUiState.Ready(track), awaitItem())

            vm.onPlaybackError("No network")
            awaitItem()

            vm.onPlaybackRetry()

            val state = awaitItem() as PlayerUiState.Ready
            assertNull(state.playback.errorMessage)
            assertTrue(state.playback.isPlaying)
            assertTrue(state.playback.isBuffering)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `consumeSeek clears pending seek target`() = runTest {
        val track = TrackFixtures.track(id = 10L)
        val repo = TestTrackRepository(track)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.state.test {
            assertEquals(PlayerUiState.Loading, awaitItem())
            assertEquals(PlayerUiState.Ready(track), awaitItem())

            vm.seekTo(45_000L)

            val seeking = awaitItem() as PlayerUiState.Ready
            assertEquals(45_000L, seeking.playback.seekTarget)

            vm.consumeSeek()

            val consumed = awaitItem() as PlayerUiState.Ready
            assertNull(consumed.playback.seekTarget)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `mark played is delayed`() = runTest {
        val track = TrackFixtures.track(id = 10L)
        val repo = TestTrackRepository(track)
        val vm = PlayerViewModel(
            trackId = 10L,
            trackRepository = repo,
            applicationScope = backgroundScope,
        )

        vm.onTrackPlayed(10L)
        runCurrent()

        assertEquals(emptyList<Long>(), repo.playedTrackIds)
        advanceTimeBy(499)
        runCurrent()
        assertEquals(emptyList<Long>(), repo.playedTrackIds)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(listOf(10L), repo.playedTrackIds)
    }

    private class TestTrackRepository(
        var track: Track?,
    ) : TrackRepository {
        private val recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
        val playedTrackIds = mutableListOf<Long>()
        var holdNextLoad: CompletableDeferred<Unit>? = null
        var loadAttempts = 0
            private set

        override fun getRecentlyPlayed(): Flow<List<Track>> = recentlyPlayed

        override suspend fun getTrack(trackId: Long): Track? {
            loadAttempts += 1
            holdNextLoad?.await()
            holdNextLoad = null
            return track?.takeIf { it.trackId == trackId }
        }

        override suspend fun markPlayed(trackId: Long) {
            playedTrackIds += trackId
        }
    }
}
