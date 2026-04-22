package com.boni.stemflow.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.boni.stemflow.core.data.mapper.toEntity
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.database.entity.RemoteKeysEntity
import com.boni.stemflow.core.database.entity.SearchResultEntity
import com.boni.stemflow.core.database.entity.TrackEntity
import com.boni.stemflow.core.network.NetworkDataSource
import retrofit2.HttpException
import java.io.IOException

const val SEARCH_PAGE_SIZE = 25

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val network: NetworkDataSource,
    private val db: StemflowDatabase,
) : RemoteMediator<Int, TrackEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrackEntity>,
    ): MediatorResult = try {
        val nextPage = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val keys = db.remoteKeysDao().keysForQuery(query)
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                keys.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        val offset = nextPage * SEARCH_PAGE_SIZE
        val tracks = network.search(term = query, limit = SEARCH_PAGE_SIZE, offset = offset)
        val endReached = tracks.size < SEARCH_PAGE_SIZE

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                db.searchResultDao().clearForQuery(query)
                db.remoteKeysDao().clearForQuery(query)
            }
            db.trackDao().upsertAll(tracks.map { it.toEntity() })

            val startPosition = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> (db.searchResultDao().maxPositionFor(query) ?: -1) + 1
                LoadType.PREPEND -> 0
            }

            val entries = tracks.mapIndexed { i, t ->
                SearchResultEntity(
                    query = query,
                    position = startPosition + i,
                    pageIndex = nextPage,
                    trackId = t.trackId,
                )
            }
            db.searchResultDao().insertAll(entries)

            db.remoteKeysDao().upsert(
                RemoteKeysEntity(
                    query = query,
                    prevKey = if (nextPage == 0) null else nextPage - 1,
                    nextKey = if (endReached) null else nextPage + 1,
                ),
            )
        }

        MediatorResult.Success(endOfPaginationReached = endReached)
    } catch (e: IOException) {
        MediatorResult.Error(e)
    } catch (e: HttpException) {
        if (e.code() == 429) MediatorResult.Success(endOfPaginationReached = true)
        else MediatorResult.Error(e)
    }
}
