package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
    size: Dp = 56.dp,
    containerColor: Color = Color.Transparent,
) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .drawBehind {
                val strokePx = 1.dp.toPx()
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val white = Color.White
                val black = Color.Black.copy(alpha = 0.55f)

                rotate(degrees = 45f, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(white, black, white, black, white),
                            center = center,
                        ),
                        radius = this@drawBehind.size.minDimension / 2f - strokePx / 2f,
                        center = center,
                        style = Stroke(width = strokePx),
                    )
                }
            }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = onSurface,
        )
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
