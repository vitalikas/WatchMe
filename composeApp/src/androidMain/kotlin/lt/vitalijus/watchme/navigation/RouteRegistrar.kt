package lt.vitalijus.watchme.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * Route configuration interface.
 *
 * Each implementation represents an app navigation route and provides:
 * - NavGraph registration via register()
 * - Route string generation via getRoute()
 *
 * Usage:
 * ```
 * // Add route to nav graph
 * PlayerRouter.registerAllRoutes(builder, navController,
 *     BrowseRouteRegistrar,
 *     StandardPlayerRouteRegistrar
 * )
 *
 * // Navigate to route
 * val route = StandardPlayerRouteRegistrar.getRoute("video123")
 * navController.navigate(route)
 * ```
 *
 * Add new routes by implementing this interface without modifying existing code (OCP).
 */
interface RouteRegistrar {

    /**
     * Registers this route with the nav graph builder.
     *
     * Called by PlayerRouter.registerAllRoutes() to add composable to navigation.
     */
    fun register(builder: NavGraphBuilder, navController: NavController)

    /**
     * Associated Screen object (route pattern and argument metadata).
     */
    val screen: Screen

    /**
     * Returns the full navigation route string.
     *
     * Handles both routes with and without arguments automatically:
     * - No arguments: getRoute() → "browse"
     * - With arguments: getRoute("abc123") → "standard_player/abc123"
     *
     * Uses screen.argumentKey to know if/what arguments are needed.
     */
    fun getRoute(arg: String? = null): String {
        return arg?.let { argValue ->
            screen.argumentKey?.let { key ->
                screen.route.replace("{$key}", argValue)
            } ?: screen.route
        } ?: screen.route
    }

    /**
     * Display name for debugging/testing.
     */
    val routeName: String
}
