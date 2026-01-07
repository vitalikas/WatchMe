package lt.vitalijus.watchme.navigation.routes

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.domain.model.PlayerType
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.browse.BrowseScreen

/**
 * Browse screen route registration
 * Implements RouteRegistrar for true OCP compliance
 *
 * Uses Screen.Browse as the single source of truth for the route pattern
 */
@OptIn(UnstableApi::class)
object BrowseRouteRegistrar : RouteRegistrar {

    private val routePattern = Screen.Browse.route

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = routePattern) {
            BrowseScreen(
                onVideoSelected = { video ->
                    val route = when (video.playerType) {
                        PlayerType.STANDARD -> StandardPlayerRouteRegistrar.getRoute(video.id)
                        PlayerType.SCTE35 -> Scte35PlayerRouteRegistrar.getRoute(video.id)
                    }
                    navController.navigate(route = route)
                },
                onAnalyticsClick = {
                    AnalyticsRouteRegistrar.navigate(navController = navController)
                }
            )
        }
    }

    override val routeName: String = "Browse"

    /**
     * Get navigation route for Browse screen (has no arguments)
     */
    override fun getRoute(value: String?): String = routePattern

    /**
     * Navigate to Browse screen
     */
    fun navigate(navController: NavController) {
        navController.navigate(routePattern)
    }
}
