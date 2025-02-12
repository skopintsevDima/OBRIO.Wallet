package ua.obrio.feature.add_transaction.presentation.usecase

import kotlinx.coroutines.delay
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase

class AddTransactionUseCaseImpl(
    private val accountRepository: AccountRepository
): AddTransactionUseCase {
    override suspend fun execute(transaction: TransactionModel): Result<Unit> {
        val userAccount = accountRepository.getUserAccount()
        val updatedBalanceBTC = userAccount.currentBalanceBTC + transaction.amountBTC
        val updatedTransactions = listOf(transaction) + userAccount.transactions
        delay(500L) // TODO: Remove
        return accountRepository.updateUserAccount(
            updatedBalanceBTC = updatedBalanceBTC,
            updatedTransactions = updatedTransactions
        )
    }
}