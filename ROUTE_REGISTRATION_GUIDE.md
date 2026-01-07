# Route Registration Guide

## Dynamic Route Registration (Truly OCP!)

### Adding Routes from ANYWHERE in the app

With our dynamic registration system, you can add new routes from **any file** without ever modifying `PlayerRouter.kt` or `Navigation.kt`!

### Example: Adding a Settings Screen

**Step 1: Create the registrar**
```kotlin
// navigation/routes/SettingsRouteRegistrar.kt
package lt.vitalijus.watchme.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import lt.vitalijus.watchme.navigation.RouteRegistrar
import lt.vitalijus.watchme.navigation.Screen
import lt.vitalijus.watchme.ui.settings.SettingsScreen

object SettingsRouteRegistrar : RouteRegistrar {
    private val routePattern = Screen.Settings.route

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable(route = routePattern) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }

    override val routeName: String = "Settings"

    fun navigate(navController: NavController) {
        navController.navigate(routePattern)
    }
}
```

**Step 2: Register from ANYWHERE**
```kotlin
// Option A: From an initialization block
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerRouter.register(SettingsRouteRegistrar)
    }
}

// Option B: From a feature module
// FeatureModuleA/src/.../di/AppModule.kt
fun configureFeatureRoutes() {
    PlayerRouter.register(SettingsRouteRegistrar)
}

// Option C: From a plugin system
// PluginsModule/src/.../analytics/AnalyticsPlugin.kt
class AnalyticsPlugin : Plugin {
    override fun initialize() {
        PlayerRouter.register(AnalyticsRouteRegistrar)
    }
}

// Option D: From anywhere!
// SomeScreen.kt
Button(onClick = {
    PlayerRouter.register(SettingsRouteRegistrar)
    navController.navigate("settings")
}) {
    Text("Enable Settings")
}
```

That's it! **Never touch PlayerRouter.kt again!**

## Comparison: Before vs After

### Before (Still required modifying PlayerRouter.kt):
```kotlin
// PlayerRouter.kt
private val routeRegistrars: List<RouteRegistrar> = listOf(
    BrowseRouteRegistrar,
    AnalyticsRouteRegistrar,
    StandardPlayerRouteRegistrar,
    Scte35PlayerRouteRegistrar,
    SettingsRouteRegistrar  // ❌ Had to modify this file!
)

// Feature modules couldn't add their own routes
// Plugin systems were impossible
```

### After (Truly OCP - modify from anywhere!):
```kotlin
// PlayerRouter.kt - NEVER CHANGES AGAIN!

// Register from your feature module
class AnalyticsFeature {
    fun initialize() {
        PlayerRouter.register(AnalyticsRouteRegistrar)
    }
}

// Register from a plugin
class PremiumPlugin {
    fun unlockFeatures() {
        PlayerRouter.register(PremiumFeaturesRouteRegistrar)
        PlayerRouter.register(SubscriptionRouteRegistrar)
    }
}

// Register conditionally
if (BuildConfig.FEATURE_X_ENABLED) {
    PlayerRouter.register(FeatureXRouteRegistrar)
}
```

## Use Cases

### 1. Feature Modules with Their Own Routes
```kotlin
// FeatureModuleA/di/FeatureModuleRoutes.kt
object FeatureModuleRoutes {
    fun registerAll() {
        PlayerRouter.register(
            FeatureRoute1Registrar,
            FeatureRoute2Registrar
        )
    }
}
```

### 2. Plugin Architecture
```kotlin
// Plugin: Analytics
class AnalyticsPlugin : Plugin {
    override fun onLoad() {
        PlayerRouter.register(AnalyticsDashboardRouteRegistrar)
    }
}

// Plugin: User Management
class UserManagementPlugin : Plugin {
    override fun onLoad() {
        PlayerRouter.register(
            UserProfileRouteRegistrar,
            UserSettingsRouteRegistrar
        )
    }
}
```

### 3. Conditional Routes
```kotlin
// Development tools
if (BuildConfig.DEBUG) {
    PlayerRouter.register(DevToolsRouteRegistrar)
    PlayerRouter.register(NetworkInspectorRouteRegistrar)
}

// Premium features
if (UserSession.isPremium) {
    PlayerRouter.register(PremiumContentRouteRegistrar)
}

// A/B testing
when (ExperimentVariant.getFor("new_ui")) {
    "A" -> PlayerRouter.register(LegacyUIRouteRegistrar)
    "B" -> PlayerRouter.register(NewUIRouteRegistrar)
}
```

### 4. Lazy Loading Routes
```kotlin
// Load feature routes on-demand
class FeatureLazyLoader {
    fun loadFeatureA() {
        PlayerRouter.register(FeatureARouteRegistrar)
    }
    
    fun loadFeatureB() {
        PlayerRouter.register(FeatureBRouteRegistrar)
    }
}
```

## Testing

```kotlin
@Test
fun `can register routes dynamically`() {
    // Initially only 4 routes
    assertEquals(4, PlayerRouter.getAllRegisteredRoutes().size)
    
    // Register a new route
    PlayerRouter.register(MockRouteRegistrar)
    
    // Now 5 routes
    assertEquals(5, PlayerRouter.getAllRegisteredRoutes().size)
}
```

## Benefits

✅ **Never modify PlayerRouter.kt** - Add routes from anywhere
✅ **Feature modules are independent** - They register their own routes
✅ **Plugin architecture possible** - Plugins can add routes dynamically
✅ **Conditional routes** - Enable/disable features at runtime
✅ **Lazy loading** - Load routes only when needed
✅ **Microservices-like approach** - Each module owns its navigation
✅ **Ultimate OCP** - Truly open for extension, closed for modification!

This is the **ultimate OCP implementation**! 🚀