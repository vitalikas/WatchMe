package lt.vitalijus.watchme.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.AnalyticsScreen

/**
 * Analytics screen route registration
 * Implements RouteRegistrar for true OCP compliance
 *
 * Uses Screen.Analytics as the single source of truth for the route pattern
 */
object AnalyticsRouteRegistrar : RouteRegistrar {

    // Route pattern from Screen object (single source of truth)
    private val routePattern = Screen.Analytics.route

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable(route = routePattern) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "Analytics"

    /**
     * Get navigation route for Analytics screen (no arguments)
     */
    override fun getRoute(value: String?): String = routePattern

    /**
     * Navigate to Analytics screen
     */
    fun navigate(navController: NavController) {
        navController.navigate(routePattern)
    }
}