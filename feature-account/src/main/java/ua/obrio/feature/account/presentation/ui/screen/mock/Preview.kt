package ua.obrio.feature.account.presentation.ui.screen.mock

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.feature.account.presentation.ui.screen.AccountViewModel
import ua.obrio.feature.account.presentation.ui.screen.UiIntent
import ua.obrio.feature.account.presentation.ui.screen.UiState
import java.time.LocalDateTime

internal class MockAccountViewModelWithState(
    state: UiState
): AccountViewModel {
    override val uiState: StateFlow<UiState> = MutableStateFlow(state)

    override fun tryHandleIntent(intent: UiIntent) {
        // Do nothing
    }
}

internal object MockUiState {
    val Idle = UiState.Idle
    val Loading = UiState.Loading
    val ErrorUnknown = UiState.Error.UnknownError("An unknown error occurred")
    val Data = UiState.Data(
        userBalanceBTC = 100.32765,
        userTransactions = listOf(
            TransactionModel(
                LocalDateTime.now(),
                5.15,
                TransactionModel.Category.TAXI
            ),
            TransactionModel(
                LocalDateTime.now(),
                -246.86938600,
                TransactionModel.Category.RESTAURANT
            ),
            TransactionModel(
                LocalDateTime.now(),
                12.35,
                TransactionModel.Category.OTHER
            ),
            TransactionModel(
                LocalDateTime.now(),
                -4.11,
                TransactionModel.Category.GROCERIES
            ),
            TransactionModel(
                LocalDateTime.now(),
                -1863.1375,
                TransactionModel.Category.ELECTRONICS
            ),
            TransactionModel(
                LocalDateTime.now(),
                267.170,
                TransactionModel.Category.TAXI
            ),
        ),
        bitcoinExchangeRateUSD = 99_999f
    )
    val DataEmptyTransactions = UiState.Data(
        userBalanceBTC = 1.33284293,
        userTransactions = emptyList(),
        bitcoinExchangeRateUSD = 99_999f
    )
    val DataZeroBitcoin = UiState.Data(
        userBalanceBTC = 0.0,
        userTransactions = listOf(
            TransactionModel(
                LocalDateTime.now(),
                5.15,
                TransactionModel.Category.TAXI
            ),
            TransactionModel(
                LocalDateTime.now(),
                -246.86938600,
                TransactionModel.Category.RESTAURANT
            ),
            TransactionModel(
                LocalDateTime.now(),
                12.35,
                TransactionModel.Category.OTHER
            ),
            TransactionModel(
                LocalDateTime.now(),
                -4.11,
                TransactionModel.Category.GROCERIES
            ),
            TransactionModel(
                LocalDateTime.now(),
                -1863.1375,
                TransactionModel.Category.ELECTRONICS
            ),
            TransactionModel(
                LocalDateTime.now(),
                267.170,
                TransactionModel.Category.TAXI
            ),
        ),
        bitcoinExchangeRateUSD = 99_999f
    )
    val DataNoExchangeRate = UiState.Data(
        userBalanceBTC = 598402.327868432,
        userTransactions = listOf(
            TransactionModel(
                LocalDateTime.now(),
                5.15,
                TransactionModel.Category.TAXI
            ),
            TransactionModel(
                LocalDateTime.now(),
                -246.86938600,
                TransactionModel.Category.RESTAURANT
            ),
            TransactionModel(
                LocalDateTime.now(),
                12.35,
                TransactionModel.Category.OTHER
            ),
            TransactionModel(
                LocalDateTime.now(),
                -4.11,
                TransactionModel.Category.GROCERIES
            ),
            TransactionModel(
                LocalDateTime.now(),
                -1863.1375,
                TransactionModel.Category.ELECTRONICS
            ),
            TransactionModel(
                LocalDateTime.now(),
                267.170,
                TransactionModel.Category.TAXI
            ),
        ),
        bitcoinExchangeRateUSD = 99_999f
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
        MockUiState.DataEmptyTransactions,
        MockUiState.DataZeroBitcoin,
        MockUiState.DataNoExchangeRate
    )
}