package ua.obrio.feature.account.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.feature.account.domain.usecase.DepositUseCase
import ua.obrio.feature.account.domain.usecase.GetBitcoinExchangeRateUseCase
import ua.obrio.feature.account.domain.usecase.GetUserAccountUseCase
import ua.obrio.feature.account.presentation.usecase.DepositUseCaseImpl
import ua.obrio.feature.account.presentation.usecase.GetBitcoinExchangeRateUseCaseImpl
import ua.obrio.feature.account.presentation.usecase.GetUserAccountUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    fun provideGetBookSummaryUseCase(
        accountRepository: AccountRepository
    ): GetUserAccountUseCase = GetUserAccountUseCaseImpl(accountRepository)

    @Provides
    fun provideGetBitcoinExchangeRateUseCase(): GetBitcoinExchangeRateUseCase =
        GetBitcoinExchangeRateUseCaseImpl()

    @Provides
    fun provideDepositUseCase(
        accountRepository: AccountRepository
    ): DepositUseCase = DepositUseCaseImpl(accountRepository)
}