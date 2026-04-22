package com.boni.stemflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boni.stemflow.core.database.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(key: RemoteKeysEntity)

    @Query("SELECT * FROM remote_keys WHERE `query` = :query")
    suspend fun keysForQuery(query: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys WHERE `query` = :query")
    suspend fun clearForQuery(query: String)
}
