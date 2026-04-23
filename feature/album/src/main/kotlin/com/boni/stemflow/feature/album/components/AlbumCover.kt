package com.boni.stemflow.feature.album.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.designsystem.theme.cover

private val CoverSize = 120.dp

@Composable
internal fun AlbumCover(
    artworkUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val coverShape = MaterialTheme.shapes.cover
    AsyncImage(
        model = artworkUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .shadow(elevation = 24.dp, shape = coverShape)
            .size(CoverSize)
            .clip(coverShape)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
    )
}

@Preview
@Composable
private fun AlbumCoverPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(40.dp),
        ) {
            AlbumCover(artworkUrl = null, contentDescription = null)
        }
    }
}
