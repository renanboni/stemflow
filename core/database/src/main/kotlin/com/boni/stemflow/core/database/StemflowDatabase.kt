package com.boni.stemflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boni.stemflow.core.database.dao.AlbumDao
import com.boni.stemflow.core.database.dao.RecentlyPlayedDao
import com.boni.stemflow.core.database.dao.RemoteKeysDao
import com.boni.stemflow.core.database.dao.SearchResultDao
import com.boni.stemflow.core.database.dao.TrackDao
import com.boni.stemflow.core.database.entity.AlbumEntity
import com.boni.stemflow.core.database.entity.RecentlyPlayedEntity
import com.boni.stemflow.core.database.entity.RemoteKeysEntity
import com.boni.stemflow.core.database.entity.SearchResultEntity
import com.boni.stemflow.core.database.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        AlbumEntity::class,
        RecentlyPlayedEntity::class,
        SearchResultEntity::class,
        RemoteKeysEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class StemflowDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun searchResultDao(): SearchResultDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
