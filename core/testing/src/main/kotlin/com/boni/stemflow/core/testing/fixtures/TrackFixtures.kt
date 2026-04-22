package com.boni.stemflow.core.testing.fixtures

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track

object TrackFixtures {
    fun track(
        id: Long = 1L,
        name: String = "Track $id",
        artistName: String = "Artist",
        artistId: Long = 100L,
        collectionId: Long = 10L,
        collectionName: String = "Album",
        previewUrl: String? = "https://example.com/preview-$id.m4a",
        artworkUrl100: String? = "https://example.com/art-$id/100x100-75.jpg",
        artworkUrl600: String? = "https://example.com/art-$id/600x600-75.jpg",
        trackTimeMillis: Long? = 210_000L,
        primaryGenreName: String? = "Pop",
        releaseDate: String? = "2020-01-01T00:00:00Z",
        trackViewUrl: String? = "https://music.apple.com/track/$id",
    ) = Track(
        trackId = id,
        name = name,
        artistName = artistName,
        artistId = artistId,
        collectionId = collectionId,
        collectionName = collectionName,
        artworkUrl100 = artworkUrl100,
        artworkUrl600 = artworkUrl600,
        previewUrl = previewUrl,
        trackTimeMillis = trackTimeMillis,
        primaryGenreName = primaryGenreName,
        releaseDate = releaseDate,
        trackViewUrl = trackViewUrl,
    )

    fun tracks(count: Int, collectionId: Long = 10L, startId: Long = 1L): List<Track> =
        (0 until count).map { track(id = startId + it, collectionId = collectionId) }

    fun album(
        collectionId: Long = 10L,
        name: String = "Album",
        trackCount: Int = 3,
    ) = Album(
        collectionId = collectionId,
        name = name,
        artistName = "Artist",
        artistId = 100L,
        artworkUrl600 = "https://example.com/albumart/600x600-75.jpg",
        releaseDate = "2020-01-01T00:00:00Z",
        trackCount = trackCount,
        genre = "Pop",
        tracks = tracks(trackCount, collectionId = collectionId),
    )
}
