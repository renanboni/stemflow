package com.boni.stemflow.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Stable
class PermissionState internal constructor(
    val permission: String,
    private val context: Context,
) {
    var isGranted: Boolean by mutableStateOf(check())
        internal set

    internal var launcher: ActivityResultLauncher<String>? = null

    fun request() {
        launcher?.launch(permission)
    }

    internal fun refresh() {
        isGranted = check()
    }

    private fun check(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    val state = remember(permission) { PermissionState(permission, context) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { state.refresh() }
    DisposableEffect(state, launcher) {
        state.launcher = launcher
        onDispose { state.launcher = null }
    }
    return state
}
