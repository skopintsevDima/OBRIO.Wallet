package ua.obrio.feature.account.presentation.usecase

import ua.obrio.common.domain.repository.BitcoinPriceRepository
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase

class GetBitcoinExchangeRateUseCaseImpl(
    private val bitcoinPriceRepository: BitcoinPriceRepository
): GetBitcoinExchangeRateUseCase {
    override suspend fun execute(): Float {
        return bitcoinPriceRepository.getPriceUSD()
    }
}