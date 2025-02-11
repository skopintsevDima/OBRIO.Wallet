package ua.obrio.feature.account.presentation.usecase

import kotlinx.coroutines.delay
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.feature.account.domain.usecase.GetUserAccountUseCase
import java.time.LocalDateTime

class GetUserAccountUseCaseImpl : GetUserAccountUseCase {
    override suspend fun execute(): AccountModel? {
        delay(500L) // TODO: Remove
        return AccountModel(
            currentAmountBTC = 125.2365672,
            transactions = listOf(
                TransactionModel(
                    LocalDateTime.now(),
                    5.15,
                    TransactionModel.Category.TAXI
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -246.86938600,
                    TransactionModel.Category.RESTAURANT
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    12.35,
                    TransactionModel.Category.OTHER
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -4.11,
                    TransactionModel.Category.GROCERIES
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -1863.1375,
                    TransactionModel.Category.ELECTRONICS
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    267.170,
                    TransactionModel.Category.TAXI
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    5.15,
                    TransactionModel.Category.TAXI
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -246.86938600,
                    TransactionModel.Category.RESTAURANT
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    12.35,
                    TransactionModel.Category.OTHER
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -4.11,
                    TransactionModel.Category.GROCERIES
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -1863.1375,
                    TransactionModel.Category.ELECTRONICS
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    267.170,
                    TransactionModel.Category.TAXI
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    5.15,
                    TransactionModel.Category.TAXI
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -246.86938600,
                    TransactionModel.Category.RESTAURANT
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    12.35,
                    TransactionModel.Category.OTHER
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -4.11,
                    TransactionModel.Category.GROCERIES
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    -1863.1375,
                    TransactionModel.Category.ELECTRONICS
                ),
                TransactionModel(
                    LocalDateTime.now(),
                    267.170,
                    TransactionModel.Category.TAXI
                ),
            )
        )
    }
}