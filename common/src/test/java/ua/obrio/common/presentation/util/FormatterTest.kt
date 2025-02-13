package ua.obrio.common.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test
import ua.obrio.common.presentation.util.Constants.UI.Common.FORMAT_TRANSACTION_DATE_TIME
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormatterTest {
    @Test
    fun `formatBalanceBTC formats large values correctly`() {
        assertEquals("100000.00", formatBalanceBTC(100000.0))
        assertEquals("99999.00", formatBalanceBTC(99999.0))
        assertEquals("10000.00", formatBalanceBTC(10000.0))
        assertEquals("9999.90", formatBalanceBTC(9999.9))
        assertEquals("1000.00", formatBalanceBTC(1000.0))
        assertEquals("999.99", formatBalanceBTC(999.99))
    }

    @Test
    fun `formatBalanceBTC formats small values correctly`() {
        assertEquals("999.99", formatBalanceBTC(999.99))
        assertEquals("100.12", formatBalanceBTC(100.12345))
        assertEquals("10.12", formatBalanceBTC(10.12345))
        assertEquals("1.01", formatBalanceBTC(1.01234))
        assertEquals("0.001234", formatBalanceBTC(0.001234))
        assertEquals("0.0001234", formatBalanceBTC(0.0001234))
    }

    @Test
    fun `formatBalanceBTC handles edge cases correctly`() {
        assertEquals("0", formatBalanceBTC(0.0))
        assertEquals("1.00", formatBalanceBTC(1.0))
        assertEquals("-1.00", formatBalanceBTC(-1.0))
        assertEquals("-1000.00", formatBalanceBTC(-1000.0))
        assertEquals("-0.0001234", formatBalanceBTC(-0.0001234))
    }

    @Test
    fun `formatTransactionBTC formats typical transaction values correctly`() {
        assertEquals("10", formatTransactionBTC(10.0))
        assertEquals("100.5", formatTransactionBTC(100.5))
        assertEquals("0.00123", formatTransactionBTC(0.00123))
    }

    @Test
    fun `formatTransactionBTC removes unnecessary trailing zeros`() {
        assertEquals("10", formatTransactionBTC(10.000000))
        assertEquals("5.1", formatTransactionBTC(5.100000))
        assertEquals("0.123", formatTransactionBTC(0.123000))
    }

    @Test
    fun `formatTransactionBTC handles edge cases correctly`() {
        assertEquals("0", formatTransactionBTC(0.0))
        assertEquals("-10", formatTransactionBTC(-10.0))
        assertEquals("-0.00123", formatTransactionBTC(-0.00123))
    }

    @Test
    fun `transactionDateTimeFormatter formats date correctly`() {
        val date = LocalDateTime.of(2025, 2, 13, 14, 30, 0)
        val expectedFormat = date.format(transactionDateTimeFormatter)
        assertEquals(expectedFormat, date.format(transactionDateTimeFormatter))
    }

    @Test
    fun `transactionDateTimeFormatter correctly parses and formats the same value`() {
        val dateString = "Feb 13, 2025 14:30"
        val formatter = DateTimeFormatter.ofPattern(FORMAT_TRANSACTION_DATE_TIME)
        val date = LocalDateTime.parse(dateString, formatter)
        val formattedString = date.format(formatter)

        assertEquals(dateString, formattedString)
    }
}