package ua.obrio.common.data.db.source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.data.mapper.toDomain
import ua.obrio.common.data.mapper.toEntity
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.model.AccountModel
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_USER_ACCOUNT_NOT_FOUND
import ua.obrio.common.presentation.util.Constants.ErrorCodes.Account.ERROR_USER_ACCOUNT_UPDATE_FAILED

class DbAccountSource(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) : AccountSource {

    init {
        createUserAccountIfNeeded()
    }

    private fun createUserAccountIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            val userAccount = accountDao.getAccount()
            if (userAccount == null) {
                accountDao.insertAccount(AccountEntity(currentBalanceBTC = 0.0))
            }
        }
    }

    override fun getUserAccountFlow(): Flow<AccountModel> {
        return accountDao.getAccountFlow().combine(
            transactionDao.getTransactionsFlow()
        ) { dbAccount, dbTransactions ->
            ensureUserAccountExists(dbAccount).toDomain(dbTransactions)
        }
    }

    override suspend fun getUserAccount(): AccountModel {
        val dbAccount = accountDao.getAccount()
        val dbTransactions = transactionDao.getTransactions()
        return ensureUserAccountExists(dbAccount).toDomain(dbTransactions)
    }

    private fun ensureUserAccountExists(dbAccount: AccountEntity?): AccountEntity {
        if (dbAccount == null) {
            throw Throwable(ERROR_USER_ACCOUNT_NOT_FOUND.toString())
        }
        return dbAccount
    }

    override suspend fun updateUserAccount(updatedAccount: AccountModel): Result<Unit> {
        return try {
            accountDao.insertAccount(updatedAccount.toEntity())
            transactionDao.clearTransactions()
            transactionDao.insertTransactions(updatedAccount.transactions.map { it.toEntity() })
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(Throwable(ERROR_USER_ACCOUNT_UPDATE_FAILED.toString()))
        }
    }
}