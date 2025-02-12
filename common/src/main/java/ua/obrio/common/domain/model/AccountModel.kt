package ua.obrio.common.domain.model

data class AccountModel(
    val currentBalanceBTC: Double,
    val transactions: List<TransactionModel>
)