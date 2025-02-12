package ua.obrio.feature.account.domain.usecase

interface DepositUseCase {
    suspend fun execute(
        currentBalanceBTC: Double,
        depositAmountBTC: Double
    ): Result<Unit>
}