package com.boni.stemflow.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.boni.stemflow.core.data.mapper.toDomain
import com.boni.stemflow.core.data.paging.SEARCH_PAGE_SIZE
import com.boni.stemflow.core.data.paging.SearchRemoteMediator
import com.boni.stemflow.core.database.StemflowDatabase
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.SearchRepository
import com.boni.stemflow.core.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class DefaultSearchRepository @Inject constructor(
    private val network: NetworkDataSource,
    private val db: StemflowDatabase,
) : SearchRepository {

    override fun searchPaging(query: String): Flow<PagingData<Track>> =
        Pager(
            config = PagingConfig(
                pageSize = SEARCH_PAGE_SIZE,
                initialLoadSize = SEARCH_PAGE_SIZE,
                prefetchDistance = SEARCH_PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            remoteMediator = SearchRemoteMediator(query, network, db),
            pagingSourceFactory = { db.searchResultDao().pagingSourceFor(query) },
        ).flow.map { data -> data.map { it.toDomain() } }
}
