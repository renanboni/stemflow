package com.boni.stemflow.feature.album.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class AlbumRoute(val albumId: Long) : NavKey
