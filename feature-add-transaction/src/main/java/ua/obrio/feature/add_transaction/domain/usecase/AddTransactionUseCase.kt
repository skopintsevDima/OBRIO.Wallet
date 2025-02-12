package ua.obrio.feature.add_transaction.domain.usecase

import ua.obrio.common.domain.model.TransactionModel

interface AddTransactionUseCase {
    suspend fun execute(transaction: TransactionModel): Result<Unit>
}