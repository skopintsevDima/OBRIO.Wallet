package ua.obrio.feature.account.domain.usecase

interface DepositUseCase {
    suspend fun execute(depositAmountBTC: Double): Result<Unit>
}