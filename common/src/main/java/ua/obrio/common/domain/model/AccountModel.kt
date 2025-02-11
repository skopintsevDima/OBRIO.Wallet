package ua.obrio.common.domain.model

data class AccountModel(
    val currentAmountBTC: Double,
    val transactions: List<TransactionModel>
)