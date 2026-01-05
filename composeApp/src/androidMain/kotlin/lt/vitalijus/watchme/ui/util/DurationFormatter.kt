package lt.vitalijus.watchme.ui.util

import java.util.Locale

/**
 * Utility function to format duration in seconds to human-readable format
 * Returns "LIVE" for zero duration (live streams)
 * Returns "H:MM:SS" for durations >= 1 hour
 * Returns "M:SS" for durations < 1 hour
 */
fun formatDuration(seconds: Long): String {
    if (seconds == 0L) return "LIVE"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    val locale = Locale.getDefault()
    return if (hours > 0) {
        String.format(
            locale,
            "%d:%02d:%02d", hours, minutes, secs
        )
    } else {
        String.format(
            locale,
            "%d:%02d", minutes, secs
        )
    }
}
