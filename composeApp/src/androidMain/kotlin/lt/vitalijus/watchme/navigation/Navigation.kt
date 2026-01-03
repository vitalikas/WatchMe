package lt.vitalijus.watchme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lt.vitalijus.watchme.model.SampleContent
import lt.vitalijus.watchme.model.VideoContent
import lt.vitalijus.watchme.ui.AnalyticsScreen
import lt.vitalijus.watchme.ui.BrowseScreen
import lt.vitalijus.watchme.ui.PlayerScreen

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
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
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
            val videoId = backStackEntry.arguments?.getString("videoId")
            val video = SampleContent.videos.find { it.id == videoId }
            
            if (video != null) {
                PlayerScreen(
                    video = video,
                    onBack = { navController.popBackStack() }
                )
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
