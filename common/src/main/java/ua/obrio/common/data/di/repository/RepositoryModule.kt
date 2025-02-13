package ua.obrio.common.data.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.repository.AccountRepositoryImpl
import ua.obrio.common.data.repository.BitcoinPriceRepositoryImpl
import ua.obrio.common.data.repository.TransactionsRepositoryImpl
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.data.source.BitcoinPriceSource
import ua.obrio.common.data.source.BitcoinPriceStorage
import ua.obrio.common.data.source.TransactionsSource
import ua.obrio.common.data.util.dataStore
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.BitcoinPriceRepository
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

    @Provides
    @Singleton
    fun provideBitcoinPriceRepository(
        @ApplicationContext context: Context,
        bitcoinPriceSource: BitcoinPriceSource,
        bitcoinPriceStorage: BitcoinPriceStorage
    ): BitcoinPriceRepository = BitcoinPriceRepositoryImpl(
        bitcoinPriceSource,
        bitcoinPriceStorage,
        context.dataStore
    )
}