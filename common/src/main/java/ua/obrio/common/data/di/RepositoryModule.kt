package ua.obrio.common.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.data.repository.AccountRepositoryImpl
import ua.obrio.common.data.source.AccountSource
import ua.obrio.common.domain.repository.AccountRepository
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
}