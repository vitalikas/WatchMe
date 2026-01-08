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

@OptIn(UnstableApi::class)
object BrowseRouteRegistrar : RouteRegistrar {

    override val screen = Screen.Browse

    override fun register(
        builder: NavGraphBuilder,
        navController: NavController
    ) {
        builder.composable(route = screen.route) {
            BrowseScreen(
                onVideoSelected = { video ->
                    val route = when (video.playerType) {
                        PlayerType.STANDARD -> StandardPlayerRouteRegistrar.getRoute(arg = video.id)
                        PlayerType.SCTE35 -> Scte35PlayerRouteRegistrar.getRoute(arg = video.id)
                    }
                    navController.navigate(route = route)
                },
                onAnalyticsClick = {
                    navController.navigate(route = Screen.Analytics.route)
                }
            )
        }
    }

    override val routeName: String = "Browse"
}
