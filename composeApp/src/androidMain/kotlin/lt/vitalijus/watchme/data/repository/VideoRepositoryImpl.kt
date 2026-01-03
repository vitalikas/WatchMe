package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository
import lt.vitalijus.watchme.model.SampleContent

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

/**
 * Remote data source (simulates API calls)
 * In production, this would use Retrofit/Ktor
 */
//class VideoRemoteDataSource {
//    suspend fun fetchVideos(): List<Video> {
//        // Simulate network delay
//        delay(500)
//
//        // Map from sample content to domain model
//        return SampleContent.videos.map { it.toDomainModel() }
//    }
//
//    suspend fun fetchVideoById(id: String): Video? {
//        delay(300)
//        return SampleContent.videos.find { it.id == id }?.toDomainModel()
//    }
//}

/**
 * Local cache (in-memory)
 * In production, this would use Room/SQLDelight
 */
class VideoCache {
    private val cache = mutableMapOf<String, Video>()
    private var lastFetchTime = 0L
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes

    fun getVideos(): List<Video> = cache.values.toList()

    fun getVideoById(id: String): Video? = cache[id]

    fun saveVideos(videos: List<Video>) {
        videos.forEach { cache[it.id] = it }
        lastFetchTime = System.currentTimeMillis()
    }

    fun saveVideo(video: Video) {
        cache[video.id] = video
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - lastFetchTime > cacheTimeout
    }

    fun clear() {
        cache.clear()
        lastFetchTime = 0
    }
}

/**
 * Mapper: Data layer -> Domain layer
 */
private fun lt.vitalijus.watchme.model.VideoContent.toDomainModel(): Video {
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
