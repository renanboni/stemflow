package com.boni.stemflow.core.domain.model

data class Album(
    val collectionId: Long,
    val name: String,
    val artistName: String,
    val artistId: Long,
    val artworkUrl600: String?,
    val releaseDate: String?,
    val trackCount: Int,
    val genre: String?,
    val tracks: List<Track>,
)
