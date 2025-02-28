package ua.obrio.feature.add_transaction.presentation.ui.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.ui.screen.base.BaseViewModel
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_ADDING_TRANSACTION_FAILED
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_INCORRECT_AMOUNT_FOR_TRANSACTION
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_INSUFFICIENT_BALANCE
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_MISSING_CATEGORY_FOR_TRANSACTION
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_NO_DATA_FOR_TRANSACTION
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Common.ERROR_UNKNOWN
import ua.obrio.common.presentation.util.errorCode
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase
import java.time.LocalDateTime
import javax.inject.Inject

interface AddTransactionViewModel {
    val uiState: StateFlow<UiState>
    fun tryHandleIntent(intent: UiIntent)
}

@HiltViewModel
class AddTransactionViewModelImpl @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    resourceProvider: ResourceProvider,
    backgroundOpsDispatcher: CoroutineDispatcher
): BaseViewModel<UiState, UiIntent, UiResult, UiEvent>(
    initialState = UiState.Data(
        strEnteredAmountBTC = "",
        selectedCategory = null
    ),
    resourceProvider,
    backgroundOpsDispatcher
), AddTransactionViewModel {
    override fun tryHandleIntent(intent: UiIntent) {
        try {
            handleIntent(intent)
        } catch (e: Throwable) {
            _uiState.value = reduce(
                _uiState.value,
                UiResult.Failure(e.errorCode(ERROR_UNKNOWN))
            )
        }
    }

    override fun handleIntent(intent: UiIntent) {
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

            is UiIntent.AddTransaction -> addTransaction()
        }
    }

    private fun addTransaction() {
        viewModelScope.launch(backgroundOpsDispatcher) {
            val uiResult = try {
                _uiState.value.asData?.run {
                    val transactionAmountBTC = strEnteredAmountBTC.toDoubleOrNull()?.takeIf { it > 0 }
                        ?: return@run UiResult.Failure(ERROR_INCORRECT_AMOUNT_FOR_TRANSACTION)
                    val transactionCategory = selectedCategory
                        ?: return@run UiResult.Failure(ERROR_MISSING_CATEGORY_FOR_TRANSACTION)

                    addTransactionUseCase.execute(
                        transactionDateTime = LocalDateTime.now(),
                        transactionAmountBTC = transactionAmountBTC,
                        transactionCategory = transactionCategory
                    ).fold(
                        onSuccess = { UiResult.Success.TransactionAdded },
                        onFailure = { UiResult.Failure(it.errorCode(ERROR_ADDING_TRANSACTION_FAILED)) }
                    )
                } ?: UiResult.Failure(ERROR_NO_DATA_FOR_TRANSACTION)
            } catch (e: Throwable) {
                UiResult.Failure(e.errorCode(ERROR_ADDING_TRANSACTION_FAILED))
            }
            _uiState.value = reduce(_uiState.value, uiResult)
        }
    }

    override fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        is UiResult.Success.EnteredAmountUpdated -> previousState.asData?.copy(
            strEnteredAmountBTC = result.strEnteredAmountBTC
        ) ?: previousState

        is UiResult.Success.SelectedCategoryUpdated -> previousState.asData?.copy(
            selectedCategory = result.selectedCategory
        ) ?: previousState

        is UiResult.Success.TransactionAdded -> UiState.Finish

        is UiResult.Failure -> {
            val onNonCriticalErrorOccurred = { errorMsg: String ->
                previousState.asData?.copy(
                    nonCriticalError = NonCriticalError(errorMsg)
                ) ?: previousState
            }

            when (result.errorCode) {
                ERROR_INCORRECT_AMOUNT_FOR_TRANSACTION -> onNonCriticalErrorOccurred(
                    resourceProvider.getString(LocalResources.Strings.ErrorIncorrectAmountForTransaction)
                )
                ERROR_INSUFFICIENT_BALANCE -> onNonCriticalErrorOccurred(
                    resourceProvider.getString(LocalResources.Strings.ErrorInsufficientBalance)
                )
                ERROR_MISSING_CATEGORY_FOR_TRANSACTION -> onNonCriticalErrorOccurred(
                    resourceProvider.getString(LocalResources.Strings.ErrorMissingCategoryForTransaction)
                )
                ERROR_ADDING_TRANSACTION_FAILED -> onNonCriticalErrorOccurred(
                    resourceProvider.getString(LocalResources.Strings.ErrorAddingTransactionFailed)
                )
                else -> result.toError(resourceProvider)
            }
        }
    }
}