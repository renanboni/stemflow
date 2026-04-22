package com.boni.stemflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val trackId: Long,
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
