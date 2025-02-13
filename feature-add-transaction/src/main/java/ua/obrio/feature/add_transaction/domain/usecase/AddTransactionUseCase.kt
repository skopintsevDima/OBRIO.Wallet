package ua.obrio.feature.add_transaction.domain.usecase

import ua.obrio.common.domain.model.TransactionModel
import java.time.LocalDateTime

interface AddTransactionUseCase {
    suspend fun execute(
        transactionDateTime: LocalDateTime,
        transactionAmountBTC: Double,
        transactionCategory: TransactionModel.Category?
    ): Result<Unit>
}