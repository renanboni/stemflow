package com.boni.stemflow

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.boni.stemflow.core.designsystem.animation.LocalSharedTransitionScope
import com.boni.stemflow.feature.album.AlbumScreen
import com.boni.stemflow.feature.album.AlbumViewModel
import com.boni.stemflow.feature.album.navigation.AlbumRoute
import com.boni.stemflow.feature.library.LibraryScreen
import com.boni.stemflow.feature.library.navigation.LibraryRoute
import com.boni.stemflow.feature.player.PlayerScreen
import com.boni.stemflow.feature.player.PlayerViewModel
import com.boni.stemflow.feature.player.navigation.PlayerRoute

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(LibraryRoute)
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                sharedTransitionScope = this,
                entryProvider = entryProvider {
                    entry<LibraryRoute> {
                        LibraryScreen(
                            onTrackClick = { trackId -> backStack.add(PlayerRoute(trackId)) },
                            onOpenAlbum = { albumId -> backStack.add(AlbumRoute(albumId)) },
                        )
                    }
                    entry<PlayerRoute> { route ->
                        val viewModel: PlayerViewModel = hiltViewModel(
                            creationCallback = { factory: PlayerViewModel.Factory ->
                                factory.create(route.trackId)
                            },
                        )
                        PlayerScreen(
                            viewModel = viewModel,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<AlbumRoute> { route ->
                        val viewModel: AlbumViewModel = hiltViewModel(
                            creationCallback = { factory: AlbumViewModel.Factory ->
                                factory.create(route.albumId)
                            },
                        )
                        AlbumScreen(
                            viewModel = viewModel,
                            onBack = { backStack.removeLastOrNull() },
                            onTrackClick = { trackId -> backStack.add(PlayerRoute(trackId)) },
                        )
                    }
                },
            )
        }
    }
}
