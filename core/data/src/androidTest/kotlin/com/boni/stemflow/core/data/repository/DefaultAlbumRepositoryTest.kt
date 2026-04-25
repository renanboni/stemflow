package com.boni.stemflow.core.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.testing.fakes.FakeNetworkDataSource
import com.boni.stemflow.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DefaultAlbumRepositoryTest {

    private lateinit var db: StemflowDatabase
    private lateinit var fakeNetwork: FakeNetworkDataSource
    private lateinit var repo: DefaultAlbumRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StemflowDatabase::class.java,
        ).allowMainThreadQueries().build()
        fakeNetwork = FakeNetworkDataSource()
        repo = DefaultAlbumRepository(
            network = fakeNetwork,
            albumDao = db.albumDao(),
            trackDao = db.trackDao(),
            db = db,
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getAlbum_emitsLoadedAlbumAfterRefresh() = runTest {
        val album = TrackFixtures.album(collectionId = 42L, trackCount = 3)
        fakeNetwork.lookupAlbums = mapOf(42L to album)

        repo.getAlbum(42L).test {
            // Drain emissions until the album is fully loaded with its tracks.
            // Room emits per-table, so intermediate states may include
            // (null album + tracks) or (album + empty tracks).
            var loaded = awaitItem()
            while (loaded == null || loaded.tracks.size != 3) {
                loaded = awaitItem()
            }
            assertEquals(42L, loaded.collectionId)
            assertEquals(3, loaded.tracks.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun refreshAlbum_throws_whenNetworkFails() {
        fakeNetwork.throwOnNext = IOException("down")
        assertThrows(IOException::class.java) {
            runBlocking { repo.refreshAlbum(999L) }
        }
    }
}
