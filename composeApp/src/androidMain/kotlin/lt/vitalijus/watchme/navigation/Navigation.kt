package lt.vitalijus.watchme.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lt.vitalijus.watchme.data.repository.KtorVideoRemoteDataSource
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.ui.AnalyticsScreen
import lt.vitalijus.watchme.ui.browse.BrowseScreen
import lt.vitalijus.watchme.ui.player.PlayerScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Browse : Screen("browse")
    object Player : Screen("player/{videoId}") {
        fun createRoute(videoId: String) = "player/$videoId"
    }
    object Analytics : Screen("analytics")
}

/**
 * Main navigation setup for the app
 */
@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Browse.route
    ) {
        // Browse/Catalog Screen
        composable(Screen.Browse.route) {
            BrowseScreen(
                onVideoSelected = { video ->
                    navController.navigate(Screen.Player.createRoute(video.id))
                },
                onAnalyticsClick = {
                    navController.navigate(Screen.Analytics.route)
                }
            )
        }

        // Player Screen
        composable(Screen.Player.route) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            var video by remember { mutableStateOf<Video?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            
            // Load video asynchronously
            LaunchedEffect(videoId) {
                isLoading = true
                video = KtorVideoRemoteDataSource().fetchVideoById(videoId)
                isLoading = false
            }
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                video != null -> {
                    PlayerScreen(
                        video = video!!,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // Analytics Screen
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
