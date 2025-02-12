package ua.obrio.common.data.di.source

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.db.source.DbAccountSource
import ua.obrio.common.data.source.AccountSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideUserAccountSource(
        accountDao: AccountDao,
        transactionDao: TransactionDao
    ): AccountSource = DbAccountSource(
        accountDao,
        transactionDao
    )
}