package ua.obrio.feature.add_transaction.presentation.ui.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Common.ERROR_UNKNOWN
import javax.inject.Inject

interface AddTransactionViewModel {
    val uiState: StateFlow<UiState>
    fun tryHandleIntent(intent: UiIntent)
}

@HiltViewModel
class AddTransactionViewModelImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val backgroundOpsDispatcher: CoroutineDispatcher,
): ViewModel(), AddTransactionViewModel {
    private val _uiState = MutableStateFlow<UiState>(
        UiState.Data(
            strEnteredAmountBTC = "",
            selectedCategory = null
        )
    )
    override val uiState: StateFlow<UiState> = _uiState

    override fun tryHandleIntent(intent: UiIntent) {
        try {
            handleIntent(intent)
        } catch (e: Throwable) {
            _uiState.value = reduce(
                _uiState.value,
                UiResult.Failure(ERROR_UNKNOWN, e.message.toString())
            )
        }
    }

    private fun handleIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.UpdateEnteredAmount -> {
                val uiResult = UiResult.Success.EnteredAmountUpdated(
                    strEnteredAmountBTC = intent.strEnteredAmountBTC
                )
                _uiState.value = reduce(_uiState.value, uiResult)
            }

            is UiIntent.UpdateSelectedCategory -> {
                val uiResult = UiResult.Success.SelectedCategoryUpdated(
                    selectedCategory = intent.selectedCategory
                )
                _uiState.value = reduce(_uiState.value, uiResult)
            }

            is UiIntent.AddTransaction -> {
                // TODO: Validate selected category and entered amount -> show error to user, if needed
                // TODO: Add transaction to user account
                val uiResult = UiResult.Success.TransactionAdded
                _uiState.value = reduce(_uiState.value, uiResult)
            }
        }
    }

    private fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        UiResult.Loading -> UiState.Loading

        is UiResult.Success.EnteredAmountUpdated -> previousState.asData?.copy(
            strEnteredAmountBTC = result.strEnteredAmountBTC
        ) ?: previousState

        is UiResult.Success.SelectedCategoryUpdated -> previousState.asData?.copy(
            selectedCategory = result.selectedCategory
        ) ?: previousState

        is UiResult.Success.TransactionAdded -> UiState.Finish

        is UiResult.Failure -> result.toError(resourceProvider)
    }
}