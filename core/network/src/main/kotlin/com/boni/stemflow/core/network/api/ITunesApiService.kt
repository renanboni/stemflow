package com.boni.stemflow.core.network.api

import com.boni.stemflow.core.network.dto.ITunesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("country") country: String = "US",
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0,
    ): ITunesResponseDto

    @GET("lookup")
    suspend fun get(
        @Query("id") id: Long,
        @Query("entity") entity: String = "song",
    ): ITunesResponseDto
}
