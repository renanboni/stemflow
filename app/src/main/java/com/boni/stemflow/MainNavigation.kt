package com.boni.stemflow

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun MainNavigation(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = resources.getString(R.string.offline_snackbar),
                duration = SnackbarDuration.Indefinite,
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    val backStack = rememberNavBackStack(LibraryRoute)
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
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
                                val playerViewModel: PlayerViewModel = hiltViewModel(
                                    creationCallback = { factory: PlayerViewModel.Factory ->
                                        factory.create(route.trackId)
                                    },
                                )
                                PlayerScreen(
                                    viewModel = playerViewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                )
                            }
                            entry<AlbumRoute> { route ->
                                val albumViewModel: AlbumViewModel = hiltViewModel(
                                    creationCallback = { factory: AlbumViewModel.Factory ->
                                        factory.create(route.albumId)
                                    },
                                )
                                AlbumScreen(
                                    viewModel = albumViewModel,
                                    onBack = { backStack.removeLastOrNull() },
                                    onTrackClick = { trackId -> backStack.add(PlayerRoute(trackId)) },
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
