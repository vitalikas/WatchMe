package lt.vitalijus.watchme.navigation.routes

import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.scte35_player.Scte35PlayerScreen

@UnstableApi
object Scte35PlayerRouteRegistrar : RouteRegistrar {

    override val screen = Screen.Scte35Player

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = screen.route) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString(screen.argumentKey)
                ?: throw IllegalArgumentException("No video ID provided")
            Scte35PlayerScreen(
                videoId = videoId,
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "SCTE-35 Player"
}
