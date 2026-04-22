package com.boni.stemflow.core.common.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

/**
 * Emits values from [source] while kicking off [refresh] in the background on
 * each subscription. Refresh errors are swallowed — subscribers continue to see
 * cached values from [source]. Use when the source is a local store that the
 * refresh step writes into, so fresh values arrive through [source] anyway.
 */
fun <T> cacheFirstFlow(
    source: Flow<T>,
    refresh: suspend () -> Unit,
): Flow<T> = channelFlow {
    launch { runCatching { refresh() } }
    source.collect { send(it) }
}
