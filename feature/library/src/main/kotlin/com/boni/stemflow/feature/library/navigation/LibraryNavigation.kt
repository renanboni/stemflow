package com.boni.stemflow.feature.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.boni.stemflow.feature.library.LibraryScreen
import kotlinx.serialization.Serializable

@Serializable
data object LibraryRoute

fun NavGraphBuilder.libraryScreen(
    onTrackClick: (Long) -> Unit,
    onOpenAlbum: (Long) -> Unit,
) {
    composable<LibraryRoute> {
        LibraryScreen(
            onTrackClick = onTrackClick,
            onOpenAlbum = onOpenAlbum,
        )
    }
}

fun NavController.navigateToLibrary() {
    navigate(LibraryRoute) { popUpTo(0) }
}
