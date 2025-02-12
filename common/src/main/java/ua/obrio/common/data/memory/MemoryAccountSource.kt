package ua.obrio.common.data.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import java.time.LocalDateTime

class MemoryAccountSource : AccountSource {
    private var userAccountState = MutableStateFlow(FAKE_ACCOUNT.copy())

    override fun getUserAccount(): Flow<AccountModel> {
        return userAccountState
    }

    override suspend fun updateUserAccount(updatedAccount: AccountModel): Result<Unit> {
        userAccountState.update { updatedAccount }
        return Result.success(Unit)
    }

    companion object {
        private val FAKE_ACCOUNT = AccountModel(
            currentBalanceBTC = 0.0,
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