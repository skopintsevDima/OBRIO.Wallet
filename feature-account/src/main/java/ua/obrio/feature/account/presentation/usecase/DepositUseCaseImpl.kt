package ua.obrio.feature.account.presentation.usecase

import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import java.time.LocalDateTime

class DepositUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val transactionsRepository: TransactionsRepository
): DepositUseCase {
    override suspend fun execute(depositAmountBTC: Double): Result<Unit> {
        val userAccount = accountRepository.getUserAccount()
        val updatedBalanceBTC = userAccount.currentBalanceBTC + depositAmountBTC
        val updateUserResult = accountRepository.updateUserAccount(
            updatedBalanceBTC = updatedBalanceBTC
        )
        return updateUserResult.fold(
            onSuccess = {
                val depositTransaction = TransactionModel(
                    dateTime = LocalDateTime.now(),
                    amountBTC = depositAmountBTC
                )
                val addTransactionResult = transactionsRepository.addTransaction(depositTransaction)
                return@fold addTransactionResult
            },
            onFailure = { updateUserResult }
        )
    }
}