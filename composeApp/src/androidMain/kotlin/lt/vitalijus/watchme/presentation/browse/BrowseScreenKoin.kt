package lt.vitalijus.watchme.presentation.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

/**
 * Browse Screen using Koin DI
 * 
 * This demonstrates the new clean way to get ViewModels with Koin.
 * Compare this to the old manual DI approach in BrowseScreen.kt
 * 
 * Benefits:
 * - Less boilerplate
 * - Automatic lifecycle management
 * - Type-safe dependency injection
 * - Easy to test (can provide test modules)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreenKoin(
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    // ✅ NEW: Get ViewModel with Koin - super clean!
    val viewModel: BrowseViewModel = koinViewModel()
    
    // Observe state
    val state by viewModel.state.collectAsState()
    
    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BrowseEffect.NavigateToPlayer -> {
                    onNavigateToPlayer(effect.videoId)
                }
                is BrowseEffect.NavigateToAnalytics -> {
                    onNavigateToAnalytics()
                }
                is BrowseEffect.ShowError -> {
                    // Handle error (show Snackbar, etc.)
                }
            }
        }
    }
    
    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "TV2 Play Demo (Koin DI)",
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.handleIntent(BrowseIntent.AnalyticsClicked) 
                    }) {
                        Icon(Icons.Default.Info, "Analytics")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.error}")
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = paddingValues,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.displayedVideos) { video ->
                        VideoCardItem(
                            video = video,
                            onClick = {
                                viewModel.handleIntent(BrowseIntent.VideoClicked(video))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoCardItem(
    video: lt.vitalijus.watchme.domain.model.Video,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = video.category,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Example of getting dependencies directly with Koin
 * (not just ViewModels)
 */
@Composable
fun ExampleDirectInjection() {
    // Get use case directly if needed
    // val getVideosUseCase: GetVideosUseCase = get()
    
    // Get repository if needed
    // val repository: VideoRepository = get()
    
    // This is less common in Compose - usually use ViewModels
}
