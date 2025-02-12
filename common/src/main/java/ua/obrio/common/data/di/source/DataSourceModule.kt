package ua.obrio.common.data.di.source

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.db.source.DbAccountSource
import ua.obrio.common.data.db.source.DbTransactionsSource
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.data.source.TransactionsSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideUserAccountSource(
        accountDao: AccountDao
    ): AccountSource = DbAccountSource(accountDao)

    @Provides
    @Singleton
    fun provideUserTransactionSource(
        transactionDao: TransactionDao
    ): TransactionsSource = DbTransactionsSource(transactionDao)
}