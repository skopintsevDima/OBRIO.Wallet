package ua.obrio.common.presentation.ui.composable

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun LoadingScreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LoadingScreenLandscape()
    } else {
        LoadingScreenPortrait()
    }
}

@Composable
private fun LoadingScreenPortrait() {
    // TODO: Implement
}

@Composable
private fun LoadingScreenLandscape() {
    // TODO: Implement
}