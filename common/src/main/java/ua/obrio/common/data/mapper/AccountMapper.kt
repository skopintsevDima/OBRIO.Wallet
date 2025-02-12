package ua.obrio.common.data.mapper

import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.data.db.entity.TransactionEntity
import ua.obrio.common.domain.model.AccountModel

fun AccountEntity.toDomain(transactions: List<TransactionEntity>): AccountModel {
    return AccountModel(
        currentBalanceBTC = this.currentBalanceBTC,
        transactions = transactions.map { it.toDomain() }
    )
}

fun AccountModel.toEntity(): AccountEntity {
    return AccountEntity(
        currentBalanceBTC = this.currentBalanceBTC
    )
}