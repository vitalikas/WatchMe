package lt.vitalijus.watchme.streaming

/**
 * Helper class for scheduling simulated SCTE-35 ad breaks at specific positions.
 *
 * State is maintained by the caller (ViewModel) to survive configuration changes.
 * This class only provides pure logic for determining when to trigger ad breaks.
 */
class SimulatedAdBreakScheduler(
    private val adBreakPositions: List<Long> = listOf(30000L, 90000L, 150000L)
) {

    /**
     * Determines if an ad break should be triggered at the given position.
     *
     * The trigger window (750ms) is wider than the check interval (500ms) to ensure we don't miss it.
     * We rely on triggeredPositions to prevent multiple triggers within the same window.
     *
     * @param currentPosition Current playback position in milliseconds
     * @param triggeredPositions Set of positions that have already been triggered
     * @param isPlayingAd Whether an ad is currently playing
     * @param scte35Detected Whether real SCTE-35 markers were detected
     * @return The position to trigger, or null if no break should be triggered
     */
    fun calculateAdBreakPosition(
        currentPosition: Long,
        triggeredPositions: Set<Long>,
        isPlayingAd: Boolean,
        scte35Detected: Boolean
    ): Long? {
        // Don't trigger if real SCTE-35 markers were detected or an ad is playing
        if (scte35Detected || isPlayingAd) {
            return null
        }

        val triggerWindowMs = 750

        return adBreakPositions.firstOrNull { breakPosition ->
            val inTriggerWindow = currentPosition >= breakPosition &&
                    currentPosition < breakPosition + triggerWindowMs
            val notYetTriggered = breakPosition !in triggeredPositions

            inTriggerWindow && notYetTriggered
        }
    }

    /**
     * Gets a list of all scheduled ad break positions.
     */
    fun getAllScheduledPositions(): List<Long> = adBreakPositions.toList()

    /**
     * Gets the next upcoming ad break position after the current position.
     */
    fun getNextAdBreakPosition(currentPosition: Long): Long? {
        return adBreakPositions.firstOrNull { it > currentPosition }
    }
}
