package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerHeader(
    title: String,
    onBack: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        },
        navigationIcon = {
            GlassIconButton(
                icon = painterResource(R.drawable.ic_back),
                contentDescription = "Back",
                onClick = onBack,
                modifier = Modifier.padding(start = 20.dp),
            )
        },
        actions = {
            GlassIconButton(
                icon = painterResource(R.drawable.ic_more_menu),
                contentDescription = "More",
                onClick = onMore,
                modifier = Modifier.padding(end = 20.dp),
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
        ),
        modifier = modifier,
    )
}

@Preview(widthDp = 414)
@Composable
private fun PlayerHeaderPreview() {
    StemflowTheme {
        Box(modifier = Modifier.background(Color.Black)) {
            PlayerHeader(
                title = "Random Access Memories",
                onBack = {},
                onMore = {},
            )
        }
    }
}
