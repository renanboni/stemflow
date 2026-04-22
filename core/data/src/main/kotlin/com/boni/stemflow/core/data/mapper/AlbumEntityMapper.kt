package com.boni.stemflow.core.data.mapper

import com.boni.stemflow.core.database.entity.AlbumEntity
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track

fun Album.toEntity(): AlbumEntity = AlbumEntity(
    collectionId = collectionId,
    name = name,
    artistName = artistName,
    artistId = artistId,
    artworkUrl600 = artworkUrl600,
    releaseDate = releaseDate,
    trackCount = trackCount,
    genre = genre,
)

fun AlbumEntity.toDomain(tracks: List<Track>): Album = Album(
    collectionId = collectionId,
    name = name,
    artistName = artistName,
    artistId = artistId,
    artworkUrl600 = artworkUrl600,
    releaseDate = releaseDate,
    trackCount = trackCount,
    genre = genre,
    tracks = tracks,
)
