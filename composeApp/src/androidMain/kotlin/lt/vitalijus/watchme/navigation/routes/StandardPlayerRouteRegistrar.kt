package lt.vitalijus.watchme.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.player.PlayerScreen

object StandardPlayerRouteRegistrar : RouteRegistrar {

    override val screen = Screen.StandardPlayer

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = screen.route) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString(screen.argumentKey)
                ?: throw IllegalArgumentException("No video ID provided")
            PlayerScreen(
                videoId = videoId,
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "Standard Player"
}
