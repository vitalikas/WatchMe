package lt.vitalijus.watchme.navigation

import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * Player Router - ULTIMATE OCP Implementation with Passthrough Registration
 *
 * Open-Closed Principle (COMPLETE - PlayerRouter is completely route-agnostic):
 * - Open for extension: Create new RouteRegistrar implementation and pass to registerAllRoutes()
 * - Closed for modification: PlayerRouter.kt never knows about specific routes
 *
 * Routes are registered from outside (Navigation.kt, feature modules, plugins, etc.)
 * PlayerRouter just passes them through to the NavGraph - complete dependency inversion!
 *
 * PlayerRouter.kt NEVER learns about Browse, Analytics, Players, etc.!
 */
@UnstableApi
object PlayerRouter {

    /**
     * Register ALL given routes with the nav graph
     * PlayerRouter is completely route-agnostic - it just registers whatever is passed to it
     *
     * Called from Navigation.kt with the app's routes:
     * ```kotlin
     * PlayerRouter.registerAllRoutes(
     *     navController = navController,
     *     builder = this@NavHost,
     *     BrowseRouteRegistrar,
     *     AnalyticsRouteRegistrar,
     *     StandardPlayerRouteRegistrar
     * )
     * ```
     *
     * PlayerRouter.kt doesn't need modification when adding new general routes!
     */
    fun registerAllRoutes(
        navController: NavController,
        builder: NavGraphBuilder,
        vararg registrars: RouteRegistrar
    ) {
        registrars.forEach { registrar ->
            registrar.register(
                builder = builder,
                navController = navController
            )
        }
    }
}
