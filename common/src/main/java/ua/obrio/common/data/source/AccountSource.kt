package ua.obrio.common.data.source

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.AccountModel

interface AccountSource {
    fun getUserAccountFlow(): Flow<AccountModel>
    fun getUserAccount(): AccountModel
    suspend fun updateUserAccount(updatedAccount: AccountModel): Result<Unit>
}