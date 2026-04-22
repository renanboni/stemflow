package com.boni.stemflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.boni.stemflow.core.database.entity.RecentlyPlayedEntity
import com.boni.stemflow.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entry: RecentlyPlayedEntity)

    @Query(
        "DELETE FROM recently_played WHERE trackId NOT IN (" +
            "SELECT trackId FROM recently_played ORDER BY playedAt DESC LIMIT :keep)",
    )
    suspend fun trimToNewest(keep: Int)

    @Transaction
    suspend fun markPlayed(trackId: Long, nowMs: Long, maxKept: Int = 20) {
        insertOrReplace(RecentlyPlayedEntity(trackId = trackId, playedAt = nowMs))
        trimToNewest(maxKept)
    }

    @Query(
        """
        SELECT tracks.* FROM tracks
        INNER JOIN recently_played ON recently_played.trackId = tracks.trackId
        ORDER BY recently_played.playedAt DESC
        """,
    )
    fun observeRecent(): Flow<List<TrackEntity>>
}
