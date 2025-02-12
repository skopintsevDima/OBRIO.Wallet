package ua.obrio.common.data.source

import androidx.paging.PagingSource
import ua.obrio.common.domain.model.TransactionModel

interface TransactionsSource {
    fun getTransactionsPaged(): PagingSource<Int, TransactionModel>
    suspend fun insertTransaction(transaction: TransactionModel)
}