package ua.obrio.common.data.di.source

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.api.coingecko.CoinGeckoApi
import ua.obrio.common.data.db.dao.AccountDao
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.db.source.DbAccountSource
import ua.obrio.common.data.db.source.DbTransactionsSource
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.data.source.BitcoinPriceSource
import ua.obrio.common.data.api.source.ApiBitcoinPriceSource
import ua.obrio.common.data.source.BitcoinPriceStorage
import ua.obrio.common.data.source.TransactionsSource
import ua.obrio.common.data.storage.source.DataStoreBitcoinPriceStorage
import ua.obrio.common.data.util.dataStore
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

    @Provides
    @Singleton
    fun provideBitcoinPriceSource(
        coinGeckoApi: CoinGeckoApi
    ): BitcoinPriceSource = ApiBitcoinPriceSource(coinGeckoApi)

    @Provides
    @Singleton
    fun provideBitcoinPriceStorage(
        @ApplicationContext context: Context
    ): BitcoinPriceStorage = DataStoreBitcoinPriceStorage(
        context.dataStore
    )
}