package ua.obrio.common.domain.repository

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel

interface AccountRepository {
    fun getUserAccount(): Flow<AccountModel>
    suspend fun updateUserAccount(newBalanceBTC: Double): Result<Unit>
    suspend fun updateUserAccount(newTransactionsList: List<TransactionModel>): Result<Unit>
}