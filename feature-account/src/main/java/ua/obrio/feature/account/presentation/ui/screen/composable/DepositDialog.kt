package ua.obrio.feature.account.presentation.ui.screen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import ua.obrio.common.presentation.util.isValidDoubleOrEmpty

@Composable
fun DepositDialog(
    enteredAmountStr: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    onDispose: (String?) -> Unit
) {
    var amount by remember { mutableStateOf(TextFieldValue(enteredAmountStr)) }
    var isAmountIncorrect by remember { mutableStateOf(false) }
    var shouldDisposeEnteredAmount by remember { mutableStateOf(false) }
    val dismiss = {
        shouldDisposeEnteredAmount = true
        onDismiss()
    }
    val confirm = { enteredAmount: Double ->
        shouldDisposeEnteredAmount = true
        onConfirm(enteredAmount)
    }

    DisposableEffect(Unit) {
        onDispose {
            val enteredAmountToSave = amount.text.takeIf { !shouldDisposeEnteredAmount }
            onDispose(enteredAmountToSave)
        }
    }

    AlertDialog(
        onDismissRequest = { dismiss() },
        title = { Text(stringResource(LocalResources.Strings.Deposit)) },
        text = {
            val keyboardController = LocalSoftwareKeyboardController.current

            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.text.isValidDoubleOrEmpty()) {
                            amount = it
                            isAmountIncorrect = false
                        }
                    },
                    label = { Text(stringResource(LocalResources.Strings.EnterAmount)) },
                    isError = isAmountIncorrect,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isAmountIncorrect) {
                    Text(
                        text = stringResource(LocalResources.Strings.InvalidAmount),
                        color = LocalResources.Colors.Red,
                        fontSize = LocalResources.Dimensions.Text.SizeTiny,
                        modifier = Modifier.padding(
                            start = LocalResources.Dimensions.Padding.Small,
                            top = LocalResources.Dimensions.Padding.Tiny
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val enteredAmount = amount.text.toDoubleOrNull()
                    if (enteredAmount != null && enteredAmount > 0) {
                        confirm(enteredAmount)
                    } else {
                        isAmountIncorrect = true
                    }
                }
            ) {
                Text(stringResource(LocalResources.Strings.Okay))
            }
        },
        dismissButton = {
            Button(onClick = { dismiss() }) {
                Text(stringResource(LocalResources.Strings.Cancel))
            }
        }
    )
}