package ua.obrio.feature.account.presentation.ui.screen

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ua.obrio.common.presentation.ui.model.NonCriticalError
import ua.obrio.common.presentation.ui.resources.LocalResources
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.ui.screen.base.BaseViewModel
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_DEPOSIT_FAILED
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_LOAD_USER_ACCOUNT
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Common.ERROR_UNKNOWN
import ua.obrio.common.presentation.util.errorCode
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase
import ua.obrio.feature.account.domain.usecase.GetUserAccountFlowUseCase
import ua.obrio.feature.account.domain.usecase.GetUserTransactionsPagedUseCase
import javax.inject.Inject

interface AccountViewModel {
    val uiState: StateFlow<UiState>
    val uiEvents: SharedFlow<UiEvent>
    fun tryHandleIntent(intent: UiIntent)
}

@HiltViewModel
class AccountViewModelImpl @Inject constructor(
    private val getUserAccountFlowUseCase: GetUserAccountFlowUseCase,
    private val getUserTransactionsPagedUseCase: GetUserTransactionsPagedUseCase,
    private val getBitcoinExchangeRateUseCase: GetBitcoinExchangeRateUseCase,
    private val depositUseCase: DepositUseCase,
    resourceProvider: ResourceProvider,
    backgroundOpsDispatcher: CoroutineDispatcher,
): BaseViewModel<UiState, UiIntent, UiResult, UiEvent>(
    initialState = UiState.Loading,
    resourceProvider,
    backgroundOpsDispatcher
), AccountViewModel {
    private var userAccountUpdatesJob: Job? = null

    private fun observeUserAccountUpdates() {
        userAccountUpdatesJob?.cancel()
        userAccountUpdatesJob = viewModelScope.launch(backgroundOpsDispatcher) {
            try {
                getUserAccountFlowUseCase.execute().collectLatest { userAccount ->
                    val bitcoinExchangeRateUSD = runCatching {
                        getBitcoinExchangeRateUseCase.execute()
                    }.getOrElse { Float.NaN }

                    val userTransactionsPaged = getUserTransactionsPagedUseCase.execute()
                        .cachedIn(viewModelScope + backgroundOpsDispatcher)

                    val uiResult = UiResult.Success.ScreenDataUpdated(
                        userAccount = userAccount,
                        userTransactionsPaged = userTransactionsPaged,
                        bitcoinExchangeRateUSD = bitcoinExchangeRateUSD
                    )
                    _uiState.value = reduce(_uiState.value, uiResult)
                }
            } catch (e: Throwable){
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Failure(e.errorCode(ERROR_LOAD_USER_ACCOUNT))
                )
            }
        }
    }

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
            is UiIntent.LoadUserAccount -> observeUserAccountUpdates()
            is UiIntent.SaveDepositEnteredAmount -> saveDepositEnteredAmount(intent.enteredAmountStr)
            is UiIntent.Deposit -> deposit(intent.amountBTC)
        }
    }

    override fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        is UiResult.Success.ScreenDataUpdated -> {
            UiState.Data(
                userBalanceBTC = result.userAccount.currentBalanceBTC,
                userTransactions = result.userTransactionsPaged,
                bitcoinExchangeRateUSD = result.bitcoinExchangeRateUSD
            )
        }

        is UiResult.Success.DepositEnteredAmountSaved -> {
            previousState.asData?.copy(
                depositEnteredAmountStr = result.enteredAmountStr
            ) ?: previousState
        }

        is UiResult.Failure -> {
            val onNonCriticalErrorOccurred = { errorMsg: String ->
                previousState.asData?.copy(
                    nonCriticalError = NonCriticalError(errorMsg)
                ) ?: previousState
            }

            when (result.errorCode) {
                ERROR_DEPOSIT_FAILED -> onNonCriticalErrorOccurred(
                    resourceProvider.getString(LocalResources.Strings.ErrorDepositFailed)
                )
                else -> result.toError(resourceProvider)
            }
        }
    }

    private fun saveDepositEnteredAmount(amountStr: String?) {
        _uiState.value = reduce(
            _uiState.value,
            UiResult.Success.DepositEnteredAmountSaved(amountStr)
        )
    }

    private fun deposit(depositAmountBTC: Double) {
        viewModelScope.launch(backgroundOpsDispatcher) {
            val depositFailed = { error: Throwable -> _uiState.value = reduce(_uiState.value,
                UiResult.Failure(error.errorCode(ERROR_DEPOSIT_FAILED))
            )}

            try {
                val depositResult = depositUseCase.execute(depositAmountBTC)

                depositResult.fold(
                    onSuccess = { _uiEvents.emit(UiEvent.DepositSucceeded) },
                    onFailure = { depositFailed(it) }
                )
            } catch (e: Throwable) {
                depositFailed(e)
            }
        }
    }
}