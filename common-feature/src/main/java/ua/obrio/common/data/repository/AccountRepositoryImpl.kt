package ua.obrio.common.data.repository

import kotlinx.coroutines.flow.Flow
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val accountSource: AccountSource
): AccountRepository {
    override fun getUserAccountFlow(): Flow<AccountModel> {
        return accountSource.getUserAccountFlow()
    }

    override suspend fun getUserAccount(): AccountModel {
        return accountSource.getUserAccount()
    }

    override suspend fun updateUserAccount(updatedBalanceBTC: Double): Result<Unit> {
        val currentAccount = getUserAccount()
        val updatedAccount = currentAccount.copy(
            currentBalanceBTC = updatedBalanceBTC
        )
        return accountSource.updateUserAccount(updatedAccount)
    }
}