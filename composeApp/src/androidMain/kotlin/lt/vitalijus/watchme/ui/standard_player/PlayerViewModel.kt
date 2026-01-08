package lt.vitalijus.watchme.ui.standard_player

import lt.vitalijus.watchme.architecture.MviViewModel
import lt.vitalijus.watchme.streaming.AdPod

/**
 * Player ViewModel (Redux-style)
 *
 * Responsibilities:
 * - Hold player UI state
 * - Handle player intents
 * - Persist state across configuration changes
 * - NO ExoPlayer management (stays in Composable)
 */
class PlayerViewModel : MviViewModel<PlayerState, PlayerIntent, PlayerEffect>(
    initialState = PlayerState()
) {

    /**
     * Redux Reducer: Pure function (State, Intent) -> State
     */
    override suspend fun reduce(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.UpdatePlaybackState -> updatePlaybackState(
                isPlaying = intent.isPlaying,
                currentPosition = intent.currentPosition,
                duration = intent.duration
            )

            is PlayerIntent.UpdateBuffering -> updateBuffering(isBuffering = intent.isBuffering)
            is PlayerIntent.UpdateQuality -> updateQuality(quality = intent.quality)
            is PlayerIntent.HandleError -> handleError(errorMessage = intent.errorMessage)
            is PlayerIntent.UpdateAdState -> updateAdState(
                isAdPlaying = intent.isAdPlaying,
                adPod = intent.adPod
            )

            is PlayerIntent.ClearError -> clearError()
        }
    }

    private fun updatePlaybackState(
        isPlaying: Boolean,
        currentPosition: Long,
        duration: Long
    ) {
        setState {
            copy(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration
            )
        }
    }

    private fun updateBuffering(isBuffering: Boolean) {
        setState {
            copy(isBuffering = isBuffering)
        }
    }

    private fun updateQuality(quality: String) {
        setState {
            copy(currentQuality = quality)
        }
    }

    private fun handleError(errorMessage: String) {
        setState {
            copy(error = errorMessage)
        }
        sendEffect(
            effect = PlayerEffect.ShowError(
                message = errorMessage
            )
        )
    }

    private fun updateAdState(
        isAdPlaying: Boolean,
        adPod: AdPod?
    ) {
        setState {
            copy(
                isAdPlaying = isAdPlaying,
                currentAdPod = adPod
            )
        }
    }

    private fun clearError() {
        setState {
            copy(error = null)
        }
    }
}
