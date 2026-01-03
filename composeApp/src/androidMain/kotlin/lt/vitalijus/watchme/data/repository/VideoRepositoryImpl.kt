package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository

/**
 * Repository implementation with caching
 * Single Source of Truth pattern
 */
class VideoRepositoryImpl(
    private val remoteDataSource: VideoRemoteDataSource,
    private val cache: VideoLocalDataSource
) : VideoRepository {

    override suspend fun getVideos(): Result<List<Video>> {
        val cached = cache.getVideos()
        if (cached.isNotEmpty() && !cache.isExpired()) {
            return Result.success(cached)
        }

        return runCatchingCancellable {
            val videos = remoteDataSource.fetchVideos()
            cache.saveVideos(videos = videos)
            videos
        }
            .recover { error ->
                // Attempt to recover using the cache.
                val fallbackCache = cache.getVideos()
                fallbackCache.ifEmpty {
                    throw error
                }
            }
    }

    override suspend fun getVideoById(id: String): Result<Video?> {
        return try {
            val video = cache.getVideoById(id)
                ?: remoteDataSource.fetchVideoById(id)

            video?.let { cache.saveVideo(it) }

            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeVideos(): Flow<List<Video>> = flow {
        // Emit cached data immediately
        val cached = cache.getVideos()
        if (cached.isNotEmpty()) {
            emit(cached)
        }

        // Fetch fresh data
        try {
            val fresh = remoteDataSource.fetchVideos()
            cache.saveVideos(fresh)
            emit(fresh)
        } catch (e: Exception) {
            // Already emitted cache, just log error
            println("Failed to fetch fresh videos: ${e.message}")
        }
    }
}
