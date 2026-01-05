package lt.vitalijus.watchme.domain.model

import lt.vitalijus.watchme.ui.util.formatDuration

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
}
