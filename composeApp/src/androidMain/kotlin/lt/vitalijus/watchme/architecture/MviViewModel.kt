package lt.vitalijus.watchme.architecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base MVI ViewModel following Redux pattern
 *
 * Architecture:
 * - State: Immutable UI state
 * - Intent: User actions/events
 * - Effect: One-time side effects (navigation, toasts, etc.)
 * - Reducer: Pure function that creates new state from old state + intent
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    // Current UI state
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    // One-time side effects (navigation, toasts, etc.)
    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /**
     * Handle user intents
     * This is the only public method to interact with ViewModel
     */
    fun handleIntent(intent: Intent) {
        viewModelScope.launch {
            reduce(intent)
        }
    }

    /**
     * Redux-style reducer
     * Pure function: (State, Intent) -> State
     */
    protected abstract suspend fun reduce(intent: Intent)

    /**
     * Update state (thread-safe)
     */
    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    /**
     * Send one-time effect
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Get current state value (for use in reducer)
     */
    protected val currentState: State
        get() = _state.value
}

/**
 * Marker interface for UI State
 * All screen states must implement this
 */
interface UiState

/**
 * Marker interface for User Intents/Actions
 * All user actions must implement this
 */
interface UiIntent

/**
 * Marker interface for Side Effects
 * All one-time effects must implement this
 */
interface UiEffect
