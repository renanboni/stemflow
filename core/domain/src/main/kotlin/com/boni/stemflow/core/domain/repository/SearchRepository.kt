package com.boni.stemflow.core.domain.repository

import androidx.paging.PagingData
import com.boni.stemflow.core.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchPaging(query: String): Flow<PagingData<Track>>
}
