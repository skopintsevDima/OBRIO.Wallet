package ua.obrio.common.data.repository

import androidx.paging.PagingSource
import ua.obrio.common.data.source.TransactionsSource
import ua.obrio.common.domain.model.TransactionModel
import ua.obrio.common.domain.repository.TransactionsRepository

class TransactionsRepositoryImpl(
    private val transactionsSource: TransactionsSource
): TransactionsRepository {
    override fun getTransactionsPaged(): PagingSource<Int, TransactionModel> {
        return transactionsSource.getTransactionsPaged()
    }

    override suspend fun addTransaction(transaction: TransactionModel): Result<Unit> {
        return runCatching { transactionsSource.insertTransaction(transaction) }
    }
}