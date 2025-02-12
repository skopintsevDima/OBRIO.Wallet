package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.delay
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.account.domain.usecase.DepositUseCase

class DepositUseCaseImpl(
    private val accountRepository: AccountRepository
): DepositUseCase {
    override suspend fun execute(
        currentBalanceBTC: Double,
        depositAmountBTC: Double
    ): Result<Unit> {
        val newBalanceBTC = currentBalanceBTC + depositAmountBTC
        delay(500L) // TODO: Remove
        val depositResult = accountRepository.updateUserAccount(newBalanceBTC)
        return depositResult
    }
}