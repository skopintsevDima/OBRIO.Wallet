package ua.obrio.common.presentation.util

import ua.obrio.common.presentation.util.Constants.UI.Common.FORMAT_TRANSACTION_DATE_TIME
import java.math.BigDecimal
import java.math.MathContext
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

val transactionDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(FORMAT_TRANSACTION_DATE_TIME)

fun formatBalanceBTC(balanceBTC: Double): String {
    val balanceBTCFormatted = BigDecimal(balanceBTC, MathContext.DECIMAL64)
        .stripTrailingZeros()
        .toPlainString()
    return if (balanceBTC.absoluteValue >= 1) {
        String.format(
            Locale.getDefault(),
            "%.3f",
            balanceBTC
        )
    } else balanceBTCFormatted
}

fun formatTransactionBTC(transactionBTC: Double): String {
    return BigDecimal(transactionBTC, MathContext.DECIMAL64)
        .stripTrailingZeros()
        .toPlainString()
}