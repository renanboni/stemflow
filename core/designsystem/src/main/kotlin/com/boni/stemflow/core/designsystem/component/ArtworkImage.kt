package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.designsystem.theme.cover

/**
 * Artwork image with the app's standard placeholder tint. Caller picks the shape and sizes the
 * image via [modifier]. Pass a positive [shadowElevation] for covers that sit on a dark surface
 * and need lift (e.g. the 120dp album cover).
 */
@Composable
fun ArtworkImage(
    url: String?,
    contentDescription: String?,
    shape: Shape,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
) {
    val placeholder = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
    val base = if (shadowElevation > 0.dp) {
        modifier.shadow(elevation = shadowElevation, shape = shape)
    } else {
        modifier
    }
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = base
            .clip(shape)
            .background(placeholder),
    )
}

@Preview
@Composable
private fun ArtworkImageLargePreview() {
    StemflowTheme {
        ArtworkImage(
            url = null,
            contentDescription = null,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.size(120.dp),
        )
    }
}

@Preview
@Composable
private fun ArtworkImageCoverPreview() {
    StemflowTheme {
        ArtworkImage(
            url = null,
            contentDescription = null,
            shape = MaterialTheme.shapes.cover,
            shadowElevation = 24.dp,
            modifier = Modifier.size(120.dp),
        )
    }
}
