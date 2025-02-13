package ua.obrio.feature.add_transaction.presentation.usecase

import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.common.presentation.util.Constants.ErrorCodes.AddTransaction.ERROR_INSUFFICIENT_BALANCE
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase
import java.time.LocalDateTime

class AddTransactionUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val transactionsRepository: TransactionsRepository
): AddTransactionUseCase {
    override suspend fun execute(
        transactionDateTime: LocalDateTime,
        transactionAmountBTC: Double,
        transactionCategory: TransactionModel.Category?
    ): Result<Unit> {
        val userAccount = accountRepository.getUserAccount()
        if (transactionAmountBTC > userAccount.currentBalanceBTC) {
            return Result.failure(Throwable(ERROR_INSUFFICIENT_BALANCE.toString()))
        }

        val transaction = TransactionModel(
            dateTime = transactionDateTime,
            amountBTC = -transactionAmountBTC,
            category = transactionCategory
        )

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