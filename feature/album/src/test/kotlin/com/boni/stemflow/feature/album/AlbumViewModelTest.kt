package com.boni.stemflow.feature.album

import app.cash.turbine.test
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.repository.AlbumRepository
import com.boni.stemflow.core.testing.coroutines.MainDispatcherRule
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlbumViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun `retry restarts album flow after error`() = runTest {
        val album = TrackFixtures.album(collectionId = 10L)
        val repo = TestAlbumRepository(album)
        val vm = AlbumViewModel(collectionId = 10L, albumRepository = repo)

        vm.state.test {
            assertEquals(AlbumLoadState.Loading, awaitItem())
            assertEquals(AlbumLoadState.Error("Network failed"), awaitItem())

            vm.retry()

            assertEquals(AlbumLoadState.Loading, awaitItem())
            assertEquals(AlbumLoadState.Ready(album), awaitItem())
            assertEquals(2, repo.loadAttempts)
            cancelAndConsumeRemainingEvents()
        }
    }

    private class TestAlbumRepository(
        private val album: Album,
    ) : AlbumRepository {
        var loadAttempts = 0
            private set

        override fun getAlbum(collectionId: Long): Flow<Album?> = flow {
            loadAttempts += 1
            if (loadAttempts == 1) throw IOException("Network failed")
            emit(album)
        }

        override suspend fun refreshAlbum(collectionId: Long) = Unit
    }
}
