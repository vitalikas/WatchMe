package lt.vitalijus.watchme.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * Interface for route registration.
 * Each route/screen implements this to register itself with the navigation system.
 *
 * This achieves true OCP:
 * - Add new route? Create new class implementing RouteRegistrar
 * - No need to modify existing classes
 */
interface RouteRegistrar {

    /**
     * Register this route with the nav graph
     * @param builder NavGraphBuilder to add the route to
     * @param navController NavController for navigation operations
     */
    fun register(builder: NavGraphBuilder, navController: NavController)

    /**
     * Get a navigation route for this screen
     *
     * Automatically handles screens with or without arguments based on Screen's argumentKey:
     * - Screens WITH arguments: getRoute("abc123") → "standard_player/abc123"
     * - Screens WITHOUT arguments: getRoute() → "browse"
     *
     * @param value Optional argument value for screens with arguments
     * @return Complete navigation route string
     */
    fun getRoute(value: String? = null): String

    /**
     * Display name for this route (for debugging/testing)
     */
    val routeName: String
}
