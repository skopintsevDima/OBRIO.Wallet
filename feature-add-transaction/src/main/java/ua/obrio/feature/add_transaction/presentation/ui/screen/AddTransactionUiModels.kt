package ua.obrio.feature.add_transaction.presentation.ui.screen

import ua.obrio.common.domain.model.TransactionModel.Category
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider

sealed class UiState {
    data object Loading: UiState()

    data class Data(
        val strEnteredAmountBTC: String,
        val selectedCategory: Category?,
        val nonCriticalError: NonCriticalError? = null
    ): UiState()

    sealed class Error(val errorMsg: String): UiState() {
        data class UnknownError(val msg: String): Error(msg)
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
    data object Loading: UiResult()

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
            else -> UiState.Error.UnknownError(
                resourceProvider.getString(LocalResources.Strings.ErrorUnknown)
            )
        }
    }
}