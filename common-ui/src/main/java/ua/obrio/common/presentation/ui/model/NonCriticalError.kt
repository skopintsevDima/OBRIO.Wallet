package ua.obrio.common.presentation.ui.model

data class NonCriticalError(
    val errorMsg: String,
    val timestamp: Long = System.currentTimeMillis()
)