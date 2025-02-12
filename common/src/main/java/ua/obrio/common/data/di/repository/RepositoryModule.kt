package ua.obrio.common.data.di.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.repository.AccountRepositoryImpl
import ua.obrio.common.data.repository.TransactionsRepositoryImpl
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.data.source.TransactionsSource
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAccountRepository(
        accountSource: AccountSource
    ): AccountRepository = AccountRepositoryImpl(
        accountSource
    )

    @Provides
    @Singleton
    fun provideTransactionsRepository(
        transactionsSource: TransactionsSource
    ): TransactionsRepository = TransactionsRepositoryImpl(
        transactionsSource
    )
}