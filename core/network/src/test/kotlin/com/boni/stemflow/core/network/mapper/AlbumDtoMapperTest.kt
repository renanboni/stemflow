package com.boni.stemflow.core.network.mapper

import com.boni.stemflow.core.network.dto.ITunesResponseDto
import com.boni.stemflow.core.network.dto.TrackDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumDtoMapperTest {

    @Test
    fun `returns null when no collection row is present`() {
        val response = ITunesResponseDto(resultCount = 0, results = emptyList())
        assertNull(response.toAlbumOrNull())
    }

    @Test
    fun `returns null when only track rows are present`() {
        val response = ITunesResponseDto(
            resultCount = 1,
            results = listOf(
                TrackDto(
                    wrapperType = "track",
                    trackId = 1L,
                    trackName = "A",
                    artistName = "Artist",
                    artistId = 100L,
                    collectionId = 10L,
                    collectionName = "Album",
                ),
            ),
        )
        assertNull(response.toAlbumOrNull())
    }

    @Test
    fun `maps header row and track children into Album`() {
        val response = ITunesResponseDto(
            resultCount = 3,
            results = listOf(
                TrackDto(
                    wrapperType = "collection",
                    collectionId = 10L,
                    collectionName = "Album",
                    artistName = "Artist",
                    artistId = 100L,
                    artworkUrl100 = "https://x/100x100-75.jpg",
                    trackCount = 2,
                    primaryGenreName = "Rock",
                    releaseDate = "2020-01-01T00:00:00Z",
                ),
                TrackDto(
                    wrapperType = "track",
                    trackId = 1L,
                    trackName = "A",
                    artistName = "Artist",
                    artistId = 100L,
                    collectionId = 10L,
                    collectionName = "Album",
                ),
                TrackDto(
                    wrapperType = "track",
                    trackId = 2L,
                    trackName = "B",
                    artistName = "Artist",
                    artistId = 100L,
                    collectionId = 10L,
                    collectionName = "Album",
                ),
            ),
        )
        val album = response.toAlbumOrNull()!!
        assertEquals(10L, album.collectionId)
        assertEquals("Album", album.name)
        assertEquals(2, album.tracks.size)
        assertEquals(listOf(1L, 2L), album.tracks.map { it.trackId })
        assertEquals("https://x/600x600-75.jpg", album.artworkUrl600)
        assertEquals("Rock", album.genre)
    }

    @Test
    fun `falls back to tracks size when trackCount is missing`() {
        val response = ITunesResponseDto(
            resultCount = 2,
            results = listOf(
                TrackDto(
                    wrapperType = "collection",
                    collectionId = 10L,
                    collectionName = "Album",
                    artistName = "Artist",
                    artistId = 100L,
                    trackCount = null,
                ),
                TrackDto(
                    wrapperType = "track",
                    trackId = 1L,
                    trackName = "A",
                    artistName = "Artist",
                    artistId = 100L,
                    collectionId = 10L,
                    collectionName = "Album",
                ),
            ),
        )
        assertEquals(1, response.toAlbumOrNull()!!.trackCount)
    }
}
