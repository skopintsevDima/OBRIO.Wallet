package ua.obrio.feature.add_transaction.presentation.ui.screen

import ua.obrio.common.domain.model.TransactionModel.Category
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.util.Constants.ErrorCodes

sealed class UiState {
    data class Data(
        val strEnteredAmountBTC: String,
        val selectedCategory: Category?,
        val nonCriticalError: NonCriticalError? = null
    ): UiState()

    sealed class Error(
        val errorMsg: String,
        val timestamp: Long
    ): UiState() {
        data class NoDataError(val msg: String): Error(msg, System.currentTimeMillis())
        data class UnknownError(val msg: String): Error(msg, System.currentTimeMillis())
    }

    data object Finish: UiState()

    val asData: Data?
        get() = this as? Data
}

sealed class UiIntent {
    data class UpdateEnteredAmount(
        val strEnteredAmountBTC: String
    ) : UiIntent()

    data class UpdateSelectedCategory(
        val selectedCategory: Category
    ) : UiIntent()

    data object AddTransaction: UiIntent()
}

sealed class UiResult {
    sealed class Success: UiResult() {
        data class EnteredAmountUpdated(
            val strEnteredAmountBTC: String
        ) : Success()

        data class SelectedCategoryUpdated(
            val selectedCategory: Category
        ) : Success()

        data object TransactionAdded: Success()
    }

    data class Failure(val errorCode: Int, val errorMsg: String = ""): UiResult() {
        fun toError(resourceProvider: ResourceProvider): UiState.Error = when (errorCode) {
            ErrorCodes.AddTransaction.ERROR_NO_DATA_FOR_TRANSACTION -> UiState.Error.NoDataError(
                resourceProvider.getString(LocalResources.Strings.ErrorNoData)
            )
            else -> UiState.Error.UnknownError(
                resourceProvider.getString(LocalResources.Strings.ErrorUnknown)
            )
        }
    }
}