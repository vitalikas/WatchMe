package lt.vitalijus.watchme.di

import lt.vitalijus.watchme.data.repository.InMemoryVideoCache
import lt.vitalijus.watchme.data.repository.KtorVideoRemoteDataSource
import lt.vitalijus.watchme.data.repository.VideoLocalDataSource
import lt.vitalijus.watchme.data.repository.VideoRemoteDataSource
import lt.vitalijus.watchme.data.repository.VideoRepositoryImpl
import lt.vitalijus.watchme.domain.repository.VideoRepository
import lt.vitalijus.watchme.domain.usecase.FilterVideosByCategoryUseCase
import lt.vitalijus.watchme.domain.usecase.GetCategoriesUseCase
import lt.vitalijus.watchme.domain.usecase.GetVideoByIdUseCase
import lt.vitalijus.watchme.domain.usecase.GetVideosUseCase
import lt.vitalijus.watchme.domain.usecase.RefreshVideosUseCase
import lt.vitalijus.watchme.domain.usecase.SearchVideosUseCase
import lt.vitalijus.watchme.ui.browse.BrowseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Data Layer Module
 * Provides repositories and data sources
 */
val dataModule = module {
    // Data sources - bind interfaces to implementations
    single<VideoLocalDataSource> { InMemoryVideoCache() }
    single<VideoRemoteDataSource> { KtorVideoRemoteDataSource() }

    // Repository
    single<VideoRepository> {
        VideoRepositoryImpl(
            remoteDataSource = get(),
            cache = get()
        )
    }
}

/**
 * Domain Layer Module
 * Provides use cases (business logic)
 */
val domainModule = module {
    // Factories - new instance each time
    factory { GetVideosUseCase(get()) }
    factory { RefreshVideosUseCase(get()) }
    factory { GetVideoByIdUseCase(get()) }
    factory { FilterVideosByCategoryUseCase() }
    factory { SearchVideosUseCase() }
    factory { GetCategoriesUseCase() }
}

/**
 * Presentation Layer Module
 * Provides ViewModels
 */
val presentationModule = module {
    // ViewModels - scoped to Activity/Fragment lifecycle
    viewModel {
        BrowseViewModel(
            getVideosUseCase = get(),
            refreshVideosUseCase = get(),
            filterVideosByCategoryUseCase = get(),
            searchVideosUseCase = get(),
            getCategoriesUseCase = get()
        )
    }

    // Add more ViewModels as you migrate:
    // viewModel { PlayerViewModel(get(), get()) }
    // viewModel { AnalyticsViewModel(get()) }
}

/**
 * Complete app modules list
 */
val appModules = listOf(
    dataModule,
    domainModule,
    presentationModule
)
