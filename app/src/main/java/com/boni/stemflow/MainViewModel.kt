package com.boni.stemflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boni.stemflow.core.common.network.ConnectivityObserver
import com.boni.stemflow.core.common.network.ConnectivityState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    connectivity: ConnectivityObserver,
) : ViewModel() {

    val isOffline: StateFlow<Boolean> = connectivity.state
        .map { it == ConnectivityState.Disconnected }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}
