package com.boni.stemflow.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val wrapperType: String? = null,
    val kind: String? = null,
    val trackId: Long? = null,
    val trackName: String? = null,
    val artistId: Long? = null,
    val artistName: String? = null,
    val collectionId: Long? = null,
    val collectionName: String? = null,
    val artworkUrl60: String? = null,
    val artworkUrl100: String? = null,
    val previewUrl: String? = null,
    val trackTimeMillis: Long? = null,
    val primaryGenreName: String? = null,
    val releaseDate: String? = null,
    val trackViewUrl: String? = null,
    val trackCount: Int? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
)
