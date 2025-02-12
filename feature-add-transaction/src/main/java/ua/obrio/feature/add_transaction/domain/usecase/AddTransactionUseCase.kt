package ua.obrio.feature.add_transaction.domain.usecase

import ua.obrio.common.domain.model.TransactionModel
import java.time.LocalDateTime

interface AddTransactionUseCase {
    suspend fun execute(
        dateTime: LocalDateTime,
        amountBTC: Double,
        category: TransactionModel.Category?
    ): Result<Unit>
}