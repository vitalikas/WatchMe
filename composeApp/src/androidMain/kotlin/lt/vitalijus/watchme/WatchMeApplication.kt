package lt.vitalijus.watchme

import android.app.Application
import lt.vitalijus.watchme.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for Koin initialization
 * 
 * Koin Setup:
 * 1. Start Koin
 * 2. Provide Android context
 * 3. Load modules
 * 4. Enable logging (for debugging)
 */
class WatchMeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Start Koin DI
        startKoin {
            // Enable logging for debugging
            androidLogger(Level.ERROR) // Change to Level.DEBUG for development
            
            // Provide Android context to Koin
            androidContext(this@WatchMeApplication)
            
            // Load all modules
            modules(appModules)
        }
    }
}
