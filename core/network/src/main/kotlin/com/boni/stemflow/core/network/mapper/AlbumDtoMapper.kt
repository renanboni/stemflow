package com.boni.stemflow.core.network.mapper

import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.network.dto.ITunesResponseDto

fun ITunesResponseDto.toAlbumOrNull(): Album? {
    val header = results.firstOrNull { it.wrapperType == "collection" } ?: return null
    val colId = header.collectionId ?: return null
    val tracks = results.mapNotNull { if (it.wrapperType == "track") it.toDomainOrNull() else null }

    return Album(
        collectionId = colId,
        name = header.collectionName.orEmpty(),
        artistName = header.artistName.orEmpty(),
        artistId = header.artistId ?: 0L,
        artworkUrl600 = upscaleArtwork600(header.artworkUrl100),
        releaseDate = header.releaseDate,
        trackCount = header.trackCount ?: tracks.size,
        genre = header.primaryGenreName,
        tracks = tracks,
    )
}
