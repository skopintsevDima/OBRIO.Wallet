package ua.obrio.feature.account.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase
import ua.obrio.feature.account.domain.usecase.GetUserAccountFlowUseCase
import ua.obrio.feature.account.domain.usecase.GetUserTransactionsPagedUseCase
import ua.obrio.feature.account.presentation.usecase.DepositUseCaseImpl
import ua.obrio.feature.account.presentation.usecase.GetBitcoinExchangeRateUseCaseImpl
import ua.obrio.feature.account.presentation.usecase.GetUserAccountFlowUseCaseImpl
import ua.obrio.feature.account.presentation.usecase.GetUserTransactionsPagedUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    fun provideGetUserAccountUseCase(
        accountRepository: AccountRepository
    ): GetUserAccountFlowUseCase = GetUserAccountFlowUseCaseImpl(accountRepository)

    @Provides
    fun provideGetUserTransactionsPagedUseCase(
        transactionsRepository: TransactionsRepository
    ): GetUserTransactionsPagedUseCase = GetUserTransactionsPagedUseCaseImpl(
        transactionsRepository
    )

    @Provides
    fun provideGetBitcoinExchangeRateUseCase(): GetBitcoinExchangeRateUseCase =
        GetBitcoinExchangeRateUseCaseImpl()

    @Provides
    fun provideDepositUseCase(
        accountRepository: AccountRepository,
        transactionsRepository: TransactionsRepository
    ): DepositUseCase = DepositUseCaseImpl(
        accountRepository,
        transactionsRepository
    )
}