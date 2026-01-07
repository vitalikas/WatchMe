package lt.vitalijus.watchme.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.player.PlayerScreen

/**
 * Standard player screen route registration
 * Implements RouteRegistrar for true OCP compliance
 *
 * Uses Screen.StandardPlayer as the single source of truth for the route pattern
 * Uses type-safe argument key from Screen to prevent typos
 */
object StandardPlayerRouteRegistrar : RouteRegistrar {

    private val routePattern = Screen.StandardPlayer.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = routePattern) { backStackEntry ->
            // Use type-safe argument key from Screen object (nullable - null for screens without args)
            val videoId = backStackEntry.arguments?.getString(Screen.StandardPlayer.argumentKey)
                ?: throw IllegalArgumentException("No video ID provided")
            PlayerScreen(
                videoId = videoId,
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "Standard Player"

    /**
     * Get navigation route for Standard Player with video ID argument
     */
    override fun getRoute(value: String?): String {
        // Build route using type-safe argument key from Screen
        return if (value == null) {
            routePattern
        } else {
            Screen.StandardPlayer.argumentKey?.let { key ->
                routePattern.replace("{$key}", value)
            } ?: routePattern
        }
    }

    /**
     * Navigate to Standard Player screen with video ID
     */
    fun navigate(navController: NavController, videoId: String) {
        navController.navigate(getRoute(videoId))
    }
}
