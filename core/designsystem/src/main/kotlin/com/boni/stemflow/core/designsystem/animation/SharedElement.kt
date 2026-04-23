package com.boni.stemflow.core.designsystem.animation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
    compositionLocalOf { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedArtwork(key: Any): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    return with(sharedScope) {
        this@sharedArtwork.sharedBounds(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
        )
    }
}
