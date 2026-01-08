package lt.vitalijus.watchme.navigation

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
