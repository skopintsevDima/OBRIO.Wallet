package ua.obrio.common.presentation.util

fun String.isValidDoubleOrEmpty(): Boolean {
    if (this.isEmpty()) {
        return true
    }
    val doubleNumber = this.toDoubleOrNull()
    return doubleNumber != null && doubleNumber >= 0
}