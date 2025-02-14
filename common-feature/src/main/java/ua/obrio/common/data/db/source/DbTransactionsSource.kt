package ua.obrio.common.data.db.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ua.obrio.common.data.db.dao.TransactionDao
import ua.obrio.common.data.mapper.toDomain
import ua.obrio.common.data.mapper.toEntity
import ua.obrio.common.data.source.TransactionsSource
import ua.obrio.common.domain.model.TransactionModel

class DbTransactionsSource(
    private val transactionDao: TransactionDao
): TransactionsSource {
    override fun getTransactionsPaged(): PagingSource<Int, TransactionModel> {
        val dbPagingSource = transactionDao.getTransactionsPaged()
        return object : PagingSource<Int, TransactionModel>() {
            override fun getRefreshKey(state: PagingState<Int, TransactionModel>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionModel> {
                return try {
                    when (val dbLoadResult = dbPagingSource.load(params)) {
                        is LoadResult.Page -> LoadResult.Page(
                            data = dbLoadResult.data.map { it.toDomain() },
                            prevKey = dbLoadResult.prevKey,
                            nextKey = dbLoadResult.nextKey
                        )
                        is LoadResult.Error -> LoadResult.Error(dbLoadResult.throwable)
                        is LoadResult.Invalid -> LoadResult.Invalid()
                    }
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }
    }

    override suspend fun insertTransaction(transaction: TransactionModel) {
        val transactionEntity = transaction.toEntity()
        transactionDao.insertTransaction(transactionEntity)
    }
}