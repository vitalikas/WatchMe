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
    private val localCache: VideoLocalDataSource
) : VideoRepository {

    override suspend fun getVideos(): Result<List<Video>> {
        return try {
            // Check cache first
            val cached = localCache.getVideos()
            if (cached.isNotEmpty() && !localCache.isExpired()) {
                return Result.success(cached)
            }

            // Fetch from remote
            val videos = remoteDataSource.fetchVideos()

            // Update cache
            localCache.saveVideos(videos)

            Result.success(videos)
        } catch (e: Exception) {
            // Fallback to cache if network fails
            val cached = localCache.getVideos()
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getVideoById(id: String): Result<Video?> {
        return try {
            val video = localCache.getVideoById(id)
                ?: remoteDataSource.fetchVideoById(id)

            video?.let { localCache.saveVideo(it) }

            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeVideos(): Flow<List<Video>> = flow {
        // Emit cached data immediately
        val cached = localCache.getVideos()
        if (cached.isNotEmpty()) {
            emit(cached)
        }

        // Fetch fresh data
        try {
            val fresh = remoteDataSource.fetchVideos()
            localCache.saveVideos(fresh)
            emit(fresh)
        } catch (e: Exception) {
            // Already emitted cache, just log error
            println("Failed to fetch fresh videos: ${e.message}")
        }
    }
}
