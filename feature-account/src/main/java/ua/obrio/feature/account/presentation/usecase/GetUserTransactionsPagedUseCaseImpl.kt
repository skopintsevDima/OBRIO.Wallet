package ua.obrio.feature.account.presentation.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.TransactionsRepository
import ua.obrio.common.presentation.util.Constants.UI.Account.TRANSACTIONS_PAGE_SIZE
import ua.obrio.feature.account.domain.usecase.GetUserTransactionsPagedUseCase

class GetUserTransactionsPagedUseCaseImpl(
    private val transactionsRepository: TransactionsRepository
): GetUserTransactionsPagedUseCase {
    override fun execute(): Flow<PagingData<TransactionModel>> {
        val transactionsPagingConfig = PagingConfig(
            pageSize = TRANSACTIONS_PAGE_SIZE,
            enablePlaceholders = false
        )
        return Pager(transactionsPagingConfig) {
            transactionsRepository.getTransactionsPaged()
        }.flow
    }
}