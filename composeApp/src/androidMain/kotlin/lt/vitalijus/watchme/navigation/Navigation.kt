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
            // <--- Add new routes here!
        )
    }
}
