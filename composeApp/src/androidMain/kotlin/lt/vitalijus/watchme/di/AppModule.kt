package lt.vitalijus.watchme.di

import lt.vitalijus.watchme.data.repository.InMemoryVideoCache
import lt.vitalijus.watchme.data.repository.KtorVideoRemoteDataSource
import lt.vitalijus.watchme.data.repository.VideoRepositoryImpl
import lt.vitalijus.watchme.domain.repository.VideoRepository
import lt.vitalijus.watchme.domain.usecase.FilterVideosByCategoryUseCase
import lt.vitalijus.watchme.domain.usecase.GetCategoriesUseCase
import lt.vitalijus.watchme.domain.usecase.GetVideoByIdUseCase
import lt.vitalijus.watchme.domain.usecase.GetVideosUseCase
import lt.vitalijus.watchme.domain.usecase.SearchVideosUseCase
import lt.vitalijus.watchme.presentation.browse.BrowseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Data Layer Module
 * Provides repositories and data sources
 */
val dataModule = module {
    // Singletons - one instance shared across app
    single { InMemoryVideoCache() }
    single { KtorVideoRemoteDataSource() }

    // Repository - single instance
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
    factory { GetVideosUseCase(repository = get()) }
    factory { GetVideoByIdUseCase(repository = get()) }
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
