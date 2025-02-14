package ua.obrio.common.presentation.ui.composable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ua.obrio.common.presentation.ui.resources.LocalResources

@Composable
fun ErrorSnackBar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        snackbar = { snackbarData ->
            Snackbar(
                action = {
                    snackbarData.visuals.actionLabel.let { actionLabel ->
                        TextButton(onClick = { snackbarData.dismiss() }) {
                            Text(
                                text = actionLabel.toString(),
                                color = LocalResources.Colors.Gray
                            )
                        }
                    }
                }
            ) {
                Text(text = snackbarData.visuals.message)
            }
        }
    )
}