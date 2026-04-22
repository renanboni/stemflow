package com.boni.stemflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val query: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
