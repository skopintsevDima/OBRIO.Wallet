package ua.obrio.common.data.mapper

import ua.obrio.common.data.db.entity.TransactionEntity
import ua.obrio.common.domain.model.TransactionModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

fun TransactionEntity.toDomain(): TransactionModel {
    return TransactionModel(
        dateTime = LocalDateTime.parse(this.dateTime, dateFormatter),
        amountBTC = this.amountBTC,
        category = this.category?.let { TransactionModel.Category.of(it) }
    )
}

fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        dateTime = this.dateTime.format(dateFormatter),
        amountBTC = this.amountBTC,
        category = this.category?.nameFormatted
    )
}