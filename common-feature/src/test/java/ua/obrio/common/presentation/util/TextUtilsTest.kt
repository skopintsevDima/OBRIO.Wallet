package ua.obrio.common.presentation.util

import org.junit.Assert.*
import org.junit.Test

class TextUtilsTest {
    @Test
    fun `isValidDoubleOrEmpty returns true for empty string`() {
        val result = "".isValidDoubleOrEmpty()
        assertTrue(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns true for valid positive double`() {
        val result = "123.45".isValidDoubleOrEmpty()
        assertTrue(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns true for valid integer`() {
        val result = "42".isValidDoubleOrEmpty()
        assertTrue(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns true for valid zero`() {
        val result = "0".isValidDoubleOrEmpty()
        assertTrue(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for negative number`() {
        val result = "-1.23".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for negative integer`() {
        val result = "-100".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for non-numeric string`() {
        val result = "abc".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for string with spaces`() {
        val result = " 123.45 ".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for string with multiple dots`() {
        val result = "12.34.56".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for string with special characters`() {
        val result = "$100.00".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for string with comma instead of dot`() {
        val result = "123,45".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns true for large double value`() {
        val result = "999999999999.999".isValidDoubleOrEmpty()
        assertTrue(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for scientific notation`() {
        val result = "1.2e3".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for NaN`() {
        val result = "NaN".isValidDoubleOrEmpty()
        assertFalse(result)
    }

    @Test
    fun `isValidDoubleOrEmpty returns false for Infinity`() {
        val result = "Infinity".isValidDoubleOrEmpty()
        assertFalse(result)
    }
}