package com.boni.stemflow.feature.player.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class PlayerRoute(val trackId: Long) : NavKey
