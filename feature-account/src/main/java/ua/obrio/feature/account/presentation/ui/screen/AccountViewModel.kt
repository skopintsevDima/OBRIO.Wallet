package ua.obrio.feature.account.presentation.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_ACCOUNT_NO_DATA
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_LOAD_ACCOUNT_DATA
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Common.ERROR_UNKNOWN
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
    private val resourceProvider: ResourceProvider,
    private val backgroundOpsDispatcher: CoroutineDispatcher,
): ViewModel(), AccountViewModel {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
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
            is UiIntent.FetchScreenData -> {
                viewModelScope.launch(backgroundOpsDispatcher) {
                    if (_uiState.value !is UiState.Data) {
                        _uiState.value = reduce(_uiState.value, UiResult.Loading)
                        _uiState.value = reduce(_uiState.value, fetchScreenData())
                    }
                }
            }
        }
    }

    private fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        UiResult.Loading -> UiState.Loading

        is UiResult.Success.ScreenDataFetched -> {
            UiState.Data(
                userBalanceBTC = result.userAccount.currentAmountBTC,
                userTransactions = result.userAccount.transactions,
                bitcoinExchangeRateUSD = result.bitcoinExchangeRateUSD
            )
        }

        is UiResult.Failure -> result.toError(resourceProvider)
    }

    private suspend fun fetchScreenData(): UiResult {
        return try {
            val userAccount = getUserAccountUseCase.execute()
            if (userAccount == null) {
                UiResult.Failure(ERROR_ACCOUNT_NO_DATA)
            } else {
                val bitcoinExchangeRateUSD = getBitcoinExchangeRateUseCase.execute() ?: Float.NaN

                UiResult.Success.ScreenDataFetched(
                    userAccount,
                    bitcoinExchangeRateUSD
                )
            }
        } catch (e: Throwable) {
            UiResult.Failure(ERROR_LOAD_ACCOUNT_DATA, e.message.toString())
        }
    }
}