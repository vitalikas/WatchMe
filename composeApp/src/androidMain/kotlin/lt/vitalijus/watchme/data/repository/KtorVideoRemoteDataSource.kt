package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.delay
import lt.vitalijus.watchme.data.mappers.toDomainModel
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.model.SampleContent

// Implements the remote data source contract
class KtorVideoRemoteDataSource : VideoRemoteDataSource {

    override suspend fun fetchVideos(): List<Video> {
        delay(1000) // Simulate network
        return SampleContent.videos.map { it.toDomainModel() }
    }

    override suspend fun fetchVideoById(id: String): Video? {
        delay(1000)
        return SampleContent.videos.find { it.id == id }?.toDomainModel()
    }
}
