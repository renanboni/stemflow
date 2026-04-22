package com.boni.stemflow.core.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class DefaultConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityObserver {

    override val state: Flow<ConnectivityState> = callbackFlow {
        val manager = context.getSystemService(ConnectivityManager::class.java)

        fun currentState(): ConnectivityState {
            val caps = manager.getNetworkCapabilities(manager.activeNetwork)
            val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            return if (online) ConnectivityState.Connected else ConnectivityState.Disconnected
        }

        trySend(currentState())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityState.Connected)
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityState.Disconnected)
            }

            override fun onUnavailable() {
                trySend(ConnectivityState.Disconnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        manager.registerNetworkCallback(request, callback)

        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
