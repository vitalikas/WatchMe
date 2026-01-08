package lt.vitalijus.watchme.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.AnalyticsScreen

object AnalyticsRouteRegistrar : RouteRegistrar {

    override val screen = Screen.Analytics

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable(route = screen.route) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "Analytics"
}
