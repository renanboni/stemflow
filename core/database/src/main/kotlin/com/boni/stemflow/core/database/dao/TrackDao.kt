package com.boni.stemflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boni.stemflow.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE trackId = :trackId")
    fun observeById(trackId: Long): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE trackId = :trackId")
    suspend fun getById(trackId: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE trackId IN (:ids)")
    suspend fun getAllByIds(ids: List<Long>): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE collectionId = :id ORDER BY trackId ASC")
    fun observeTracksForCollection(id: Long): Flow<List<TrackEntity>>
}
