package ua.obrio.feature.add_transaction.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.obrio.common.domain.repository.AccountRepository
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.feature.add_transaction.domain.usecase.AddTransactionUseCase
import ua.obrio.feature.add_transaction.presentation.usecase.AddTransactionUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    fun provideAddTransactionUseCase(
        accountRepository: AccountRepository,
        transactionsRepository: TransactionsRepository
    ): AddTransactionUseCase = AddTransactionUseCaseImpl(
        accountRepository,
        transactionsRepository
    )
}