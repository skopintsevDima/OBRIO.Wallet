package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.delay
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import java.time.LocalDateTime

class DepositUseCaseImpl(
    private val accountRepository: AccountRepository
): DepositUseCase {
    override suspend fun execute(depositAmountBTC: Double): Result<Unit> {
        val userAccount = accountRepository.getUserAccount()
        val updatedBalanceBTC = userAccount.currentBalanceBTC + depositAmountBTC
        val depositTransaction = TransactionModel(
            dateTime = LocalDateTime.now(),
            amountBTC = depositAmountBTC
        )
        val updatedTransactions = listOf(depositTransaction) + userAccount.transactions
        delay(500L) // TODO: Remove
        return accountRepository.updateUserAccount(
            updatedBalanceBTC = updatedBalanceBTC,
            updatedTransactions = updatedTransactions
        )
    }
}