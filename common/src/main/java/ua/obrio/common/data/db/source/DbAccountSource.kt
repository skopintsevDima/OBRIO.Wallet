package ua.obrio.common.data.db.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.data.mapper.toDomain
import ua.obrio.common.data.mapper.toEntity
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_USER_ACCOUNT_NOT_CREATED
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_USER_ACCOUNT_UPDATE_FAILED

class DbAccountSource(
    private val accountDao: AccountDao
) : AccountSource {

    override fun getUserAccountFlow(): Flow<AccountModel> {
        return accountDao.getAccountFlow().map { dbAccount ->
            ensureUserAccountExists(dbAccount).toDomain()
        }
    }

    override suspend fun getUserAccount(): AccountModel {
        val dbAccount = accountDao.getAccount()
        return ensureUserAccountExists(dbAccount).toDomain()
    }

    private suspend fun ensureUserAccountExists(dbAccount: AccountEntity?): AccountEntity {
        if (dbAccount == null) {
            val newUserAccount = AccountEntity(currentBalanceBTC = 0.0)
            return runCatching { accountDao.insertAccount(newUserAccount) }.fold(
                onSuccess = { newUserAccount },
                onFailure = { throw Throwable(ERROR_USER_ACCOUNT_NOT_CREATED.toString()) }
            )
        }
        return dbAccount
    }

    override suspend fun updateUserAccount(updatedAccount: AccountModel): Result<Unit> {
        return try {
            accountDao.updateAccount(updatedAccount.toEntity())
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(Throwable(ERROR_USER_ACCOUNT_UPDATE_FAILED.toString()))
        }
    }
}