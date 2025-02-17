package ua.obrio.common.presentation.ui.screen.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import ua.obrio.common.presentation.ui.resources.provider.ResourceProvider

abstract class BaseViewModel<S : BaseUiState, I : BaseUiIntent, R : BaseUiResult, E: BaseUiEvent>(
    initialState: S,
    protected val resourceProvider: ResourceProvider,
    protected val backgroundOpsDispatcher: CoroutineDispatcher
): ViewModel() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState

    protected val _uiEvents = MutableSharedFlow<E>()
    val uiEvents: SharedFlow<E> = _uiEvents.asSharedFlow()

    abstract fun tryHandleIntent(intent: I)

    protected abstract fun handleIntent(intent: I)

    protected abstract fun reduce(previousState: S, result: R): S
}
