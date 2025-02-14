package ua.obrio.common.presentation.util

fun Throwable.errorCode(fallbackErrorCode: Int): Int = message?.toIntOrNull() ?: fallbackErrorCode