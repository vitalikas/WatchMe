package lt.vitalijus.watchme.domain.model

import java.util.Locale

/**
 * Domain model for Video
 * Clean architecture: Domain layer is independent of data/UI layers
 */
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: Long,
    val category: String,
    val hasDrm: Boolean,
    val hasAds: Boolean,
    val drmLicenseUrl: String? = null
) {
    val durationFormatted: String
        get() = formatDuration(duration)

    val isLive: Boolean
        get() = duration == 0L

    private fun formatDuration(seconds: Long): String {
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
}
