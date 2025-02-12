package ua.obrio.feature.add_transaction.presentation.usecase

import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase
import java.time.LocalDateTime

class AddTransactionUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val transactionsRepository: TransactionsRepository
): AddTransactionUseCase {
    override suspend fun execute(
        dateTime: LocalDateTime,
        amountBTC: Double,
        category: TransactionModel.Category?
    ): Result<Unit> {
        val transaction = TransactionModel(
            dateTime = dateTime,
            amountBTC = amountBTC,
            category = category
        )

        val userAccount = accountRepository.getUserAccount()
        val updatedBalanceBTC = userAccount.currentBalanceBTC + transaction.amountBTC
        val updateUserResult = accountRepository.updateUserAccount(
            updatedBalanceBTC = updatedBalanceBTC
        )
        return updateUserResult.fold(
            onSuccess = {
                val addTransactionResult = transactionsRepository.addTransaction(transaction)
                return@fold addTransactionResult
            },
            onFailure = { updateUserResult }
        )
    }
}