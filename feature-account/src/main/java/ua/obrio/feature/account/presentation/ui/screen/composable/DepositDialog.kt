package ua.obrio.feature.account.presentation.ui.screen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import ua.obrio.common.presentation.ui.resources.LocalResources

@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(LocalResources.Strings.Deposit)) },
        text = {
            val keyboardController = LocalSoftwareKeyboardController.current

            Column {
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(LocalResources.Strings.EnterAmount)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val enteredAmount = amount.text.toDoubleOrNull()
                    if (enteredAmount != null && enteredAmount > 0) {
                        onConfirm(enteredAmount)
                    } else {
                        // TODO: Show error
                    }
                }
            ) {
                Text(stringResource(LocalResources.Strings.Okay))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(LocalResources.Strings.Cancel))
            }
        }
    )
}