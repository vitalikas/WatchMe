package lt.vitalijus.watchme.domain.usecase

import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository

/**
 * Use Case: Get video by ID
 */
class GetVideoByIdUseCase(
    private val repository: VideoRepository
) {

    suspend operator fun invoke(videoId: String): Result<Video?> {
        return repository.getVideoById(id = videoId)
    }
}
