package ua.obrio.common.domain.model

import ua.obrio.common.presentation.util.transactionDateTimeFormatter
import java.time.LocalDateTime

data class TransactionModel(
    val dateTime: LocalDateTime,
    val amountBTC: Double,
    val category: Category
) {
    enum class Category {
        GROCERIES, TAXI, ELECTRONICS, RESTAURANT, OTHER;

        val nameFormatted: String
            get() = this.name.lowercase()

        companion object {
            fun of(categoryNameFormatted: String): Category {
                return entries.find { it.nameFormatted == categoryNameFormatted } ?: OTHER
            }
        }
    }

    val dateTimeFormatted: String
        get() = this.dateTime.format(transactionDateTimeFormatter)
}
