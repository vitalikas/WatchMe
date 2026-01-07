package lt.vitalijus.watchme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import lt.vitalijus.watchme.navigation.routes.AnalyticsRouteRegistrar
import lt.vitalijus.watchme.navigation.routes.BrowseRouteRegistrar
import lt.vitalijus.watchme.navigation.routes.Scte35PlayerRouteRegistrar
import lt.vitalijus.watchme.navigation.routes.StandardPlayerRouteRegistrar

/**
 * Navigation screen routes (single source of truth for route patterns)
 *
 * Screen objects provide type-safe route constants and argument keys that can be used throughout the app.
 * RouteRegistrars use these as the source of truth to avoid duplication.
 *
 * Screens without arguments have argumentKey = null
 * Screens with arguments have argumentKey = the key name (e.g., "videoId")
 */
sealed class Screen(
    val route: String,
    val argumentKey: String? = null
) {

    object Browse : Screen(route = "browse")  // No arguments → argumentKey = null

    object StandardPlayer : Screen(
        route = "standard_player/{videoId}",
        argumentKey = "videoId"  // Has argument → type-safe key
    )

    object Scte35Player : Screen(
        route = "scte35_player/{videoId}",
        argumentKey = "videoId"  // Has argument → type-safe key
    )

    object Analytics : Screen(route = "analytics")  // No arguments → argumentKey = null
}

/**
 * Main navigation setup for the app
 *
 * Navigation.kt is the central place where all app routes are assembled.
 * PlayerRouter.kt is route-agnostic and simply registers whatever routes are passed to it.
 *
 * To add new routes, just add them to the list passed to PlayerRouter.registerAllRoutes()
 * PlayerRouter.kt doesn't need modification!
 */
@Composable
@androidx.media3.common.util.UnstableApi
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Browse.route
    ) {
        PlayerRouter.registerAllRoutes(
            navController = navController,
            builder = this@NavHost,
            BrowseRouteRegistrar,
            AnalyticsRouteRegistrar,
            StandardPlayerRouteRegistrar,
            Scte35PlayerRouteRegistrar
            // Add new routes here! PlayerRouter.kt stays unchanged
        )
    }
}
