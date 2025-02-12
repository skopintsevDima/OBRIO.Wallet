package ua.obrio.feature.account.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ua.obrio.common.domain.model.TransactionModel

interface GetUserTransactionsPagedUseCase {
    fun execute(): Flow<PagingData<TransactionModel>>
}