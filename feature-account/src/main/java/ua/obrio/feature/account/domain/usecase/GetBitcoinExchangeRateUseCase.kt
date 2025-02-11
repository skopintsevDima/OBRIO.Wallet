package ua.obrio.feature.account.domain.usecase

interface GetBitcoinExchangeRateUseCase {
    suspend fun execute(): Float?
}