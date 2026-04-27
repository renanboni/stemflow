package com.boni.stemflow.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.boni.stemflow.core.data.paging.ITunesSearchPagingSource
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.domain.repository.SearchRepository
import com.boni.stemflow.core.network.ITunesRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DefaultSearchRepository @Inject constructor(
    private val network: ITunesRemoteDataSource,
) : SearchRepository {

    override fun searchPaging(query: String): Flow<PagingData<Track>> =
        Pager(
            config = PagingConfig(
                pageSize = ITunesSearchPagingSource.DEFAULT_PAGE_SIZE,
                initialLoadSize = ITunesSearchPagingSource.DEFAULT_PAGE_SIZE,
                prefetchDistance = ITunesSearchPagingSource.DEFAULT_PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { ITunesSearchPagingSource(query, network) },
        ).flow
}
