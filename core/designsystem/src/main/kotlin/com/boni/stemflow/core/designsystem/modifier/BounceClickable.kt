package com.boni.stemflow.core.designsystem.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch

@Composable
fun Modifier.bounceClickable(
    enabled: Boolean = true,
    pressedScale: Float = 0.94f,
    pressDurationMs: Int = 100,
    releaseDurationMs: Int = 160,
    onClick: () -> Unit,
): Modifier {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)

    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .semantics(mergeDescendants = true) {
            if (enabled) {
                role = Role.Button
                onClick {
                    currentOnClick()
                    true
                }
            }
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = {
                    val pressJob = scope.launch {
                        scale.animateTo(pressedScale, tween(pressDurationMs))
                    }
                    val released = tryAwaitRelease()
                    pressJob.cancel()
                    scale.animateTo(1f, tween(releaseDurationMs))
                    if (released) currentOnClick()
                },
            )
        }
}
