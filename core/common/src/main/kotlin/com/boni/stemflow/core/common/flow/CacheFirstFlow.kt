package com.boni.stemflow.core.common.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Emits values from [source] while kicking off [refresh] in the background on
 * each subscription. If [refresh] fails before [source] has produced a non-null
 * value, the exception is propagated downstream so the caller can render an
 * error state. Once a non-null value has been emitted, subsequent refresh
 * failures are swallowed — subscribers keep seeing cached data.
 */
fun <T : Any> cacheFirstFlow(
    source: Flow<T?>,
    refresh: suspend () -> Unit,
): Flow<T?> = channelFlow {
    val hasCacheHit = AtomicBoolean(false)
    launch {
        try {
            refresh()
        } catch (e: Throwable) {
            if (!hasCacheHit.get()) close(e)
        }
    }
    source.collect { value ->
        if (value != null) hasCacheHit.set(true)
        send(value)
    }
}
