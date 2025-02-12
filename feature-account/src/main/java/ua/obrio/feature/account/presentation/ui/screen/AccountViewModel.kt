package ua.obrio.feature.account.presentation.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_DEPOSIT_FAILED
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_LOAD_BITCOIN_PRICE
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_LOAD_USER_ACCOUNT
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Common.ERROR_UNKNOWN
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase
import ua.obrio.feature.account.domain.usecase.GetUserAccountUseCase
import javax.inject.Inject

interface AccountViewModel {
    val uiState: StateFlow<UiState>
    fun tryHandleIntent(intent: UiIntent)
}

@HiltViewModel
class AccountViewModelImpl @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getBitcoinExchangeRateUseCase: GetBitcoinExchangeRateUseCase,
    private val depositUseCase: DepositUseCase,
    private val resourceProvider: ResourceProvider,
    private val backgroundOpsDispatcher: CoroutineDispatcher,
): ViewModel(), AccountViewModel {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    override val uiState: StateFlow<UiState> = _uiState

    init {
        observeUserAccountUpdates()
    }

    private fun observeUserAccountUpdates() {
        viewModelScope.launch(backgroundOpsDispatcher) {
            getUserAccountUseCase.execute()
                .catch {
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Failure(ERROR_LOAD_USER_ACCOUNT, it.message.toString())
                    )
                }.collectLatest { userAccount ->
                    val uiResult = try {
                        val bitcoinExchangeRateUSD = getBitcoinExchangeRateUseCase.execute()

                        UiResult.Success.ScreenDataUpdated(
                            userAccount = userAccount,
                            bitcoinExchangeRateUSD = bitcoinExchangeRateUSD
                        )
                    } catch (e: Throwable) {
                        UiResult.Failure(ERROR_LOAD_BITCOIN_PRICE, e.message.toString())
                    }
                    _uiState.value = reduce(_uiState.value, uiResult)
                }
        }
    }

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
            is UiIntent.Deposit -> {
                viewModelScope.launch(backgroundOpsDispatcher) {
                    _uiState.value = reduce(_uiState.value, UiResult.Loading)
                    _uiState.value = reduce(_uiState.value, deposit(intent.amountBTC))
                }
            }
        }
    }

    private fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        UiResult.Loading -> UiState.Loading

        is UiResult.Success.ScreenDataUpdated -> {
            UiState.Data(
                userBalanceBTC = result.userAccount.currentBalanceBTC,
                userTransactions = result.userAccount.transactions,
                bitcoinExchangeRateUSD = result.bitcoinExchangeRateUSD
            )
        }

        is UiResult.Success.DepositSucceeded -> {
            // TODO: Show success message to the user (as a side effect/UiEvent)
            previousState
        }

        is UiResult.Failure -> result.toError(resourceProvider)
    }

    private suspend fun deposit(depositAmountBTC: Double): UiResult {
        try {
            val depositResult = depositUseCase.execute(depositAmountBTC)

            return depositResult.fold(
                onSuccess = { UiResult.Success.DepositSucceeded },
                onFailure = { UiResult.Failure(ERROR_DEPOSIT_FAILED, it.message.toString()) }
            )
        } catch (e: Throwable) {
            return UiResult.Failure(ERROR_DEPOSIT_FAILED, e.message.toString())
        }
    }
}