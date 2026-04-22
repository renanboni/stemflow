package com.boni.stemflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.boni.stemflow.feature.album.navigation.AlbumRoute
import com.boni.stemflow.feature.library.LibraryScreen
import com.boni.stemflow.feature.library.navigation.LibraryRoute
import com.boni.stemflow.feature.player.navigation.PlayerRoute

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(LibraryRoute)
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<LibraryRoute> {
                LibraryScreen(
                    onTrackClick = { trackId -> backStack.add(PlayerRoute(trackId)) },
                    onOpenAlbum = { albumId -> backStack.add(AlbumRoute(albumId)) },
                )
            }
            entry<PlayerRoute> { route ->
                PlaceholderScreen("Player — track ${route.trackId}")
            }
            entry<AlbumRoute> { route ->
                PlaceholderScreen("Album — ${route.albumId}")
            }
        },
    )
}

@Composable
private fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
