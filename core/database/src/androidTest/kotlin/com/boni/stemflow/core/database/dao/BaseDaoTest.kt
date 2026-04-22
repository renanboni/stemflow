package com.boni.stemflow.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.database.entity.TrackEntity
import org.junit.After
import org.junit.Before

abstract class BaseDaoTest {
    protected lateinit var db: StemflowDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StemflowDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    protected fun trackEntity(id: Long, collectionId: Long = 100L) = TrackEntity(
        trackId = id,
        name = "Track $id",
        artistName = "Artist",
        artistId = 1L,
        collectionId = collectionId,
        collectionName = "Album",
        artworkUrl100 = null,
        artworkUrl600 = null,
        previewUrl = null,
        trackTimeMillis = null,
        primaryGenreName = null,
        releaseDate = null,
        trackViewUrl = null,
    )
}
