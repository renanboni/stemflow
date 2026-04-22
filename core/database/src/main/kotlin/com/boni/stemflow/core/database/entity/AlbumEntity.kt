package com.boni.stemflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val collectionId: Long,
    val name: String,
    val artistName: String,
    val artistId: Long,
    val artworkUrl600: String?,
    val releaseDate: String?,
    val trackCount: Int,
    val genre: String?,
)
