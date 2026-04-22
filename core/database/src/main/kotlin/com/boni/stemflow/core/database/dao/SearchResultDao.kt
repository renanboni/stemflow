package com.boni.stemflow.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boni.stemflow.core.database.entity.SearchResultEntity
import com.boni.stemflow.core.database.entity.TrackEntity

@Dao
interface SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SearchResultEntity>)

    @Query("DELETE FROM search_results WHERE `query` = :query")
    suspend fun clearForQuery(query: String)

    @Query(
        """
        SELECT tracks.* FROM tracks
        INNER JOIN search_results ON search_results.trackId = tracks.trackId
        WHERE search_results.`query` = :query
        ORDER BY search_results.position ASC
        """,
    )
    fun pagingSourceFor(query: String): PagingSource<Int, TrackEntity>

    @Query("SELECT MAX(position) FROM search_results WHERE `query` = :query")
    suspend fun maxPositionFor(query: String): Int?

    @Query("SELECT COUNT(*) FROM search_results WHERE `query` = :query")
    suspend fun countFor(query: String): Int
}
