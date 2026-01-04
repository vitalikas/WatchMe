package lt.vitalijus.watchme.data.repository

import lt.vitalijus.watchme.domain.model.Video

// Implements the local data source contract
class InMemoryVideoCache : VideoLocalDataSource {

    private val cache = mutableMapOf<String, Video>()
    private var lastFetchTime = 0L
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes

    override fun getVideos(): List<Video> = cache.values.toList()

    override fun getVideoById(id: String): Video? = cache[id]

    override fun saveVideos(videos: List<Video>) {
        videos.forEach { cache[it.id] = it }
        lastFetchTime = System.currentTimeMillis()
    }

    override fun saveVideo(video: Video) {
        cache[video.id] = video
    }

    override fun isExpired(): Boolean =
        System.currentTimeMillis() - lastFetchTime > cacheTimeout

    override fun invalidate() {
        // Mark cache as expired without clearing data
        // This allows fallback if network fails
        lastFetchTime = 0L
    }

    override fun clear() {
        // Completely remove all cached data
        cache.clear()
        lastFetchTime = 0L
    }
}
