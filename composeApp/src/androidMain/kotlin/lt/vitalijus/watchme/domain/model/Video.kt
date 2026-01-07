package lt.vitalijus.watchme.domain.model

import lt.vitalijus.watchme.ui.util.formatDuration

/**
 * Player type for routing
 */
enum class PlayerType {
    STANDARD,
    SCTE35
}

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
    val playerType: PlayerType,
    val hasDrm: Boolean,
    val hasAds: Boolean,
    val drmLicenseUrl: String? = null
) {
    val durationFormatted: String
        get() = formatDuration(duration)

    val category: String
        get() = when (playerType) {
            PlayerType.STANDARD -> "Standard"
            PlayerType.SCTE35 -> "SCTE-35"
        }
}
