package lt.vitalijus.watchme.data.dto

/**
 * Data Transfer Object for Video
 * Represents video data from API/data source before mapping to domain model
 */
data class VideoDto(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: Long, // in seconds
    val category: String,
    val hasDrm: Boolean = false,
    val drmLicenseUrl: String? = null,
    val hasAds: Boolean = false // For LAR (Linear Ad Replacement) demonstration
)
