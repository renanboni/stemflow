package com.boni.stemflow.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "search_results",
    primaryKeys = ["query", "position"],
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("trackId"), Index("query")],
)
data class SearchResultEntity(
    val query: String,
    val position: Int,
    val pageIndex: Int,
    val trackId: Long,
)
