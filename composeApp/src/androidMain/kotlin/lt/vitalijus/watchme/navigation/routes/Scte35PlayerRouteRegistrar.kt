package lt.vitalijus.watchme.navigation.routes

import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.player.Scte35PlayerScreen

/**
 * SCTE-35 player screen route registration
 * Implements RouteRegistrar for true OCP compliance
 *
 * Uses Screen.Scte35Player as the single source of truth for the route pattern
 * Uses type-safe argument key from Screen to prevent typos
 */
@UnstableApi
object Scte35PlayerRouteRegistrar : RouteRegistrar {

    private val routePattern = Screen.Scte35Player.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = routePattern) { backStackEntry ->
            // Use type-safe argument key from Screen object (nullable - null for screens without args)
            val videoId = backStackEntry.arguments?.getString(Screen.Scte35Player.argumentKey)
                ?: throw IllegalArgumentException("No video ID provided")
            Scte35PlayerScreen(
                videoId = videoId,
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "SCTE-35 Player"

    /**
     * Get navigation route for SCTE-35 Player with video ID argument
     */
    override fun getRoute(value: String?): String {
        // Build route using type-safe argument key from Screen
        return if (value == null) {
            routePattern
        } else {
            Screen.Scte35Player.argumentKey?.let { key ->
                routePattern.replace("{$key}", value)
            } ?: routePattern
        }
    }

    /**
     * Navigate to SCTE-35 Player screen with video ID
     */
    fun navigate(navController: NavController, videoId: String) {
        navController.navigate(getRoute(videoId))
    }
}
