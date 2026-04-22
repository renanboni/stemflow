package com.boni.stemflow.core.network.mapper

import com.boni.stemflow.core.network.dto.TrackDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackDtoMapperTest {

    @Test
    fun `maps a complete TrackDto to domain Track`() {
        val dto = TrackDto(
            wrapperType = "track",
            kind = "song",
            trackId = 1L,
            trackName = "Upside Down",
            artistId = 909253L,
            artistName = "Jack Johnson",
            collectionId = 120954021L,
            collectionName = "Sing-a-Longs",
            artworkUrl100 = "https://img.example.com/a/b/c/mzi.qzdqwsel.100x100-75.jpg",
            previewUrl = "https://example.com/p.m4a",
            trackTimeMillis = 210743L,
            primaryGenreName = "Rock",
            releaseDate = "2006-01-01T00:00:00Z",
            trackViewUrl = "https://music.apple.com/t/1",
        )

        val track = dto.toDomainOrNull()!!

        assertEquals(1L, track.trackId)
        assertEquals("Upside Down", track.name)
        assertEquals("Jack Johnson", track.artistName)
        assertEquals(120954021L, track.collectionId)
        assertEquals(
            "https://img.example.com/a/b/c/mzi.qzdqwsel.600x600-75.jpg",
            track.artworkUrl600,
        )
    }

    @Test
    fun `returns null when trackId is missing`() {
        val dto = TrackDto(trackName = "no id")
        assertNull(dto.toDomainOrNull())
    }

    @Test
    fun `returns null when wrapperType is collection`() {
        val dto = TrackDto(
            wrapperType = "collection",
            trackId = 1L,
            trackName = "x",
            artistName = "a",
            artistId = 1L,
            collectionId = 1L,
            collectionName = "c",
        )
        assertNull(dto.toDomainOrNull())
    }

    @Test
    fun `artworkUrl600 is null when artworkUrl100 is null`() {
        val dto = TrackDto(
            wrapperType = "track",
            trackId = 1L,
            trackName = "n",
            artistName = "a",
            artistId = 1L,
            collectionId = 1L,
            collectionName = "c",
            artworkUrl100 = null,
        )
        assertNull(dto.toDomainOrNull()!!.artworkUrl600)
    }

    @Test
    fun `artworkUrl600 is returned as-is when 100x100 token is absent`() {
        val dto = TrackDto(
            wrapperType = "track",
            trackId = 1L,
            trackName = "n",
            artistName = "a",
            artistId = 1L,
            collectionId = 1L,
            collectionName = "c",
            artworkUrl100 = "https://example.com/no-size-token.jpg",
        )
        assertEquals(
            "https://example.com/no-size-token.jpg",
            dto.toDomainOrNull()!!.artworkUrl600,
        )
    }
}
