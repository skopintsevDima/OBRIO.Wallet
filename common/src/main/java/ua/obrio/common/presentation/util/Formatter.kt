package ua.obrio.common.presentation.util

import ua.obrio.common.presentation.util.Constants.UI.Common.FORMAT_TRANSACTION_DATE_TIME
import java.math.BigDecimal
import java.math.MathContext
import java.time.format.DateTimeFormatter
import java.util.Locale

val transactionDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(FORMAT_TRANSACTION_DATE_TIME)

fun formatBalanceBTC(balanceBTC: Double): String {
    val stringFormat = when {
        balanceBTC >= 100000 -> "%.0f"
        balanceBTC >= 10000 -> "%.1f"
        balanceBTC >= 1000 -> "%.2f"
        else -> "%.5g"
    }
    return String.format(
        Locale.getDefault(),
        stringFormat,
        balanceBTC
    )
}

fun formatTransactionBTC(transactionBTC: Double): String {
    return BigDecimal(transactionBTC, MathContext.DECIMAL64)
        .stripTrailingZeros()
        .toPlainString()
}