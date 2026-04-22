package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
fun GlassIconButton(
    icon: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        border = BorderStroke(1.dp, onSurface.copy(alpha = 0.20f)),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = onSurface.copy(alpha = 0.10f),
            contentColor = onSurface,
        ),
    ) {
        Icon(painter = icon, contentDescription = contentDescription)
    }
}

@Preview
@Composable
private fun GlassIconButtonPreview() {
    StemflowTheme {
        GlassIconButton(
            icon = painterResource(R.drawable.ic_back),
            contentDescription = "Back",
            onClick = {},
        )
    }
}
