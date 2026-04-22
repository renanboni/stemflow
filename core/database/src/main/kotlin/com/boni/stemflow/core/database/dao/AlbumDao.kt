package com.boni.stemflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boni.stemflow.core.database.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE collectionId = :id")
    fun observeById(id: Long): Flow<AlbumEntity?>
}
