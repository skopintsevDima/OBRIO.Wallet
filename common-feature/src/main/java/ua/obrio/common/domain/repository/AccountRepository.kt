package ua.obrio.common.domain.repository

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel

interface AccountRepository {
    fun getUserAccountFlow(): Flow<AccountModel>
    suspend fun getUserAccount(): AccountModel
    suspend fun updateUserAccount(updatedBalanceBTC: Double): Result<Unit>
}