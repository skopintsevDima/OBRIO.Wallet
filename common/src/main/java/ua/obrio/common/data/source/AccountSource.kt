package ua.obrio.common.data.source

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel

interface AccountSource {
    fun getUserAccount(): Flow<AccountModel>
    suspend fun updateUserAccount(updatedAccount: AccountModel): Result<Unit>
}