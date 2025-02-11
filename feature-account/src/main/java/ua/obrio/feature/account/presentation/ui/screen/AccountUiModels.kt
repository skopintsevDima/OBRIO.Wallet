package ua.obrio.feature.account.presentation.ui.screen

import androidx.compose.runtime.Stable
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider

sealed class UiState {
    data object Idle: UiState()
    data object Loading: UiState()

    data class Data(
        val userBalanceBTC: Double,
        @Stable val userTransactions: List<TransactionModel>,
        val bitcoinExchangeRateUSD: Float,
        val nonCriticalError: NonCriticalError? = null
    ): UiState()

    sealed class Error(val errorMsg: String): UiState() {
        data class UnknownError(val msg: String): Error(msg)
    }

    val asData: Data?
        get() = this as? Data
}

sealed class UiIntent {
    data object FetchScreenData: UiIntent()
}

sealed class UiResult {
    data object Loading: UiResult()

    sealed class Success: UiResult() {
        data class ScreenDataFetched(
            val userAccount: AccountModel,
            val bitcoinExchangeRateUSD: Float
        ): Success()
    }

    data class Failure(val errorCode: Int, val errorMsg: String = ""): UiResult() {
        fun toError(resourceProvider: ResourceProvider): UiState.Error = when (errorCode) {
            else -> UiState.Error.UnknownError(
                resourceProvider.getString(LocalResources.Strings.ErrorUnknown)
            )
        }
    }
}