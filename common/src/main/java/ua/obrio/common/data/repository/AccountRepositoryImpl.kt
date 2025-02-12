package ua.obrio.common.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val accountSource: AccountSource
): AccountRepository {
    override fun getUserAccount(): Flow<AccountModel> {
        return accountSource.getUserAccount()
    }

    override suspend fun updateUserAccount(newBalanceBTC: Double): Result<Unit> {
        val updatedAccount = getUserAccount().firstOrNull()?.copy(
            currentBalanceBTC = newBalanceBTC
        )
        return updateUserAccountOrFail(updatedAccount)
    }

    override suspend fun updateUserAccount(newTransactionsList: List<TransactionModel>): Result<Unit> {
        val updatedAccount = getUserAccount().firstOrNull()?.copy(
            transactions = newTransactionsList
        )
        return updateUserAccountOrFail(updatedAccount)
    }

    private suspend fun updateUserAccountOrFail(updatedAccount: AccountModel?): Result<Unit> {
        return if (updatedAccount != null) {
             accountSource.updateUserAccount(updatedAccount)
        } else {
            Result.failure(Throwable("User account not found!")) // TODO: Remove hardcode
        }
    }
}