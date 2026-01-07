package lt.vitalijus.watchme.data.mappers

import lt.vitalijus.watchme.data.dto.VideoDto
import lt.vitalijus.watchme.domain.model.Video

/**
 * Maps data layer DTO to domain model
 * This is where data transformation happens
 */
fun VideoDto.toDomainModel(): Video {
    return Video(
        id = id,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        duration = duration,
        playerType = playerType,  // Type-safe player type (was category)
        hasDrm = hasDrm,
        hasAds = hasAds,
        drmLicenseUrl = drmLicenseUrl
    )
}
