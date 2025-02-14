package ua.obrio.common.domain.repository

import androidx.paging.PagingSource
import ua.obrio.common.domain.model.TransactionModel

interface TransactionsRepository {
    fun getTransactionsPaged(): PagingSource<Int, TransactionModel>
    suspend fun addTransaction(transaction: TransactionModel): Result<Unit>
}