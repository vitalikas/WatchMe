package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.delay
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.model.SampleContent
import lt.vitalijus.watchme.model.VideoContent

// Implements the remote data source contract
class KtorVideoRemoteDataSource : VideoRemoteDataSource {
    override suspend fun fetchVideos(): List<Video> {
        delay(500) // Simulate network
        return SampleContent.videos.map { it.toDomainModel() }
    }

    override suspend fun fetchVideoById(id: String): Video? {
        delay(300)
        return SampleContent.videos.find { it.id == id }?.toDomainModel()
    }

    // Move the mapper function here, as it's specific to this data source
    private fun VideoContent.toDomainModel(): Video {
        return Video(
            id = id,
            title = title,
            description = description,
            thumbnailUrl = thumbnailUrl,
            videoUrl = videoUrl,
            duration = duration,
            category = category,
            hasDrm = drmConfig != null,
            hasAds = hasAds,
            drmLicenseUrl = drmConfig?.licenseUrl
        )
    }
}
