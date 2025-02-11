package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.delay
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase

class GetBitcoinExchangeRateUseCaseImpl : GetBitcoinExchangeRateUseCase {
    override suspend fun execute(): Float? {
        delay(500L) // TODO: Remove
        return 96_874f
    }
}