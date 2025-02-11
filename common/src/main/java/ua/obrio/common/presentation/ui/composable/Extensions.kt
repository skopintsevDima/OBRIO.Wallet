package ua.obrio.common.presentation.ui.composable

import android.util.Log
import androidx.compose.material3.SnackbarHostState

suspend fun SnackbarHostState.showSnackBarSafe(
    message: String,
    actionLabel: String,
    loggingTag: String
) {
    try {
        this.showSnackbar(message, actionLabel)
    } catch (e: Throwable) {
        Log.e(loggingTag, "Failed to show SnackBar: ${e.stackTraceToString()}")
    }
}