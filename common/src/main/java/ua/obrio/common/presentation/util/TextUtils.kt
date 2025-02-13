package ua.obrio.common.presentation.util

fun String.isValidDoubleOrEmpty(): Boolean {
    if (this.isEmpty()) {
        return true
    }

    val isNumeric = this.all { it.isDigit() || it == '.' }
    if (!isNumeric) {
        return false
    }

    val doubleNumber = this.toDoubleOrNull()
    return doubleNumber != null && doubleNumber >= 0
}