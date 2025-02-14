package ua.obrio.common.data.mapper

import ua.obrio.common.data.db.entity.AccountEntity
import ua.obrio.common.domain.model.AccountModel

fun AccountEntity.toDomain(): AccountModel {
    return AccountModel(
        id = this.id,
        currentBalanceBTC = this.currentBalanceBTC
    )
}

fun AccountModel.toEntity(): AccountEntity {
    return AccountEntity(
        id = this.id,
        currentBalanceBTC = this.currentBalanceBTC
    )
}