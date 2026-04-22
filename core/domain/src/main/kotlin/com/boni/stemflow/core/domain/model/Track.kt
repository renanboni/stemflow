package com.boni.stemflow.core.domain.model

data class Track(
    val trackId: Long,
    val name: String,
    val artistName: String,
    val artistId: Long,
    val collectionId: Long,
    val collectionName: String,
    val artworkUrl100: String?,
    val artworkUrl600: String?,
    val previewUrl: String?,
    val trackTimeMillis: Long?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val trackViewUrl: String?,
)
