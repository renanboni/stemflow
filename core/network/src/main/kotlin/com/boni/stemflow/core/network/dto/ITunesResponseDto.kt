package com.boni.stemflow.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ITunesResponseDto(
    val resultCount: Int = 0,
    val results: List<TrackDto> = emptyList(),
)
