package ua.obrio.feature.account.presentation.ui.screen

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.ui.screen.base.BaseUiEvent
import ua.obrio.common.presentation.ui.screen.base.BaseUiIntent
import ua.obrio.common.presentation.ui.screen.base.BaseUiResult
import ua.obrio.common.presentation.ui.screen.base.BaseUiState
import ua.obrio.common.presentation.util.Constants.ErrorCodes

sealed class UiState: BaseUiState() {
    data object Loading: UiState()

    data class Data(
        val userBalanceBTC: Double,
        val userTransactions: Flow<PagingData<TransactionModel>>,
        val bitcoinExchangeRateUSD: Float,
        val depositEnteredAmountStr: String? = null,
        val nonCriticalError: NonCriticalError? = null
    ): UiState()

    sealed class Error(
        val errorMsg: String,
        val timestamp: Long
    ): UiState() {
        data class LoadUserAccountError(
            val msg: String,
            val ts: Long = System.currentTimeMillis()
        ): Error(msg, ts)

        data class UnknownError(
            val msg: String,
            val ts: Long = System.currentTimeMillis()
        ): Error(msg, ts)
    }

    val asData: Data?
        get() = this as? Data
}

sealed class UiIntent: BaseUiIntent() {
    data object LoadUserAccount: UiIntent()
    data class SaveDepositEnteredAmount(val enteredAmountStr: String?): UiIntent()
    data class Deposit(val amountBTC: Double): UiIntent()
}

sealed class UiResult: BaseUiResult() {
    sealed class Success: UiResult() {
        data class ScreenDataUpdated(
            val userAccount: AccountModel,
            val userTransactionsPaged: Flow<PagingData<TransactionModel>>,
            val bitcoinExchangeRateUSD: Float
        ): Success()

        data class DepositEnteredAmountSaved(
            val enteredAmountStr: String?
        ): Success()
    }

    data class Failure(
        val errorCode: Int,
        val errorMsg: String = ""
    ): UiResult() {
        fun toError(resourceProvider: ResourceProvider): UiState.Error = when (errorCode) {
            ErrorCodes.Account.ERROR_LOAD_USER_ACCOUNT -> UiState.Error.LoadUserAccountError(
                resourceProvider.getString(LocalResources.Strings.ErrorLoadUserAccount)
            )
            else -> UiState.Error.UnknownError(
                resourceProvider.getString(LocalResources.Strings.ErrorUnknown)
            )
        }
    }
}

sealed class UiEvent: BaseUiEvent() {
    data object DepositSucceeded: UiEvent()
}