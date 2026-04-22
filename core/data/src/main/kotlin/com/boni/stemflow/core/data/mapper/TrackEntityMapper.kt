package com.boni.stemflow.core.data.mapper

import com.boni.stemflow.core.database.entity.TrackEntity
import com.boni.stemflow.core.domain.model.Track

fun TrackEntity.toDomain(): Track = Track(
    trackId = trackId,
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

fun Track.toEntity(): TrackEntity = TrackEntity(
    trackId = trackId,
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
