package com.boni.stemflow.core.common.network

import kotlinx.coroutines.flow.Flow

enum class ConnectivityState { Connected, Disconnected }

interface ConnectivityObserver {
    val state: Flow<ConnectivityState>
}
