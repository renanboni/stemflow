package com.boni.stemflow.core.network.mapper

import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.dto.TrackDto

private const val SOURCE_SIZE_TOKEN = "100x100"
private const val TARGET_SIZE_TOKEN = "600x600"

internal fun upscaleArtwork600(artworkUrl100: String?): String? =
    artworkUrl100?.replace(SOURCE_SIZE_TOKEN, TARGET_SIZE_TOKEN)

fun TrackDto.toDomainOrNull(): Track? {
    if (wrapperType != null && wrapperType != "track") return null
    val id = trackId ?: return null
    val name = trackName ?: return null
    val artist = artistName ?: return null
    val artistIdentifier = artistId ?: return null
    val colId = collectionId ?: return null
    val colName = collectionName ?: return null

    return Track(
        trackId = id,
        name = name,
        artistName = artist,
        artistId = artistIdentifier,
        collectionId = colId,
        collectionName = colName,
        artworkUrl100 = artworkUrl100,
        artworkUrl600 = upscaleArtwork600(artworkUrl100),
        previewUrl = previewUrl,
        trackTimeMillis = trackTimeMillis,
        primaryGenreName = primaryGenreName,
        releaseDate = releaseDate,
        trackViewUrl = trackViewUrl,
    )
}
