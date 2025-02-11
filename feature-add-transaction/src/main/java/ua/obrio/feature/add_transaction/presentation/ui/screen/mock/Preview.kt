package ua.obrio.feature.add_transaction.presentation.ui.screen.mock

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.feature.add_transaction.presentation.ui.screen.AddTransactionViewModel
import ua.obrio.feature.add_transaction.presentation.ui.screen.UiIntent
import ua.obrio.feature.add_transaction.presentation.ui.screen.UiState

internal class MockAddTransactionViewModelWithState(
    state: UiState
): AddTransactionViewModel {
    override val uiState: StateFlow<UiState> = MutableStateFlow(state)

    override fun tryHandleIntent(intent: UiIntent) {
        // Do nothing
    }
}

internal object MockUiState {
    val Loading = UiState.Loading
    val ErrorUnknown = UiState.Error.UnknownError("An unknown error occurred")
    val Data = UiState.Data(
        strEnteredAmountBTC = 3253.2863.toString(),
        selectedCategory = TransactionModel.Category.GROCERIES
    )
    val DataNoSelectedCategory = UiState.Data(
        strEnteredAmountBTC = 17.25.toString(),
        selectedCategory = null
    )
    val DataNoAmountEntered = UiState.Data(
        strEnteredAmountBTC = "",
        selectedCategory = TransactionModel.Category.TAXI
    )
}

internal class ErrorStatePreviewProvider : PreviewParameterProvider<UiState.Error> {
    override val values = sequenceOf(
        MockUiState.ErrorUnknown,
        // TODO: Add more errors
    )
}

internal class DataStatePreviewProvider : PreviewParameterProvider<UiState.Data> {
    override val values = sequenceOf(
        MockUiState.Data,
        MockUiState.DataNoSelectedCategory,
        MockUiState.DataNoAmountEntered,
    )
}