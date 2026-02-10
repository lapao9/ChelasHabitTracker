package pt.isel.pdm.chatr

import android.app.Application
import pt.isel.pdm.chatr.data.HabitsRepository

/**
 * Custom Application class used as a Service Locator for dependency injection.
 * This follows the manual dependency injection pattern taught in PDM course (Week 5).
 */
class CHaTrApplication : Application() {
    
    /**
     * Repository instance, lazily initialized.
     * Available throughout the application lifecycle.
     */
    val habitsRepository: HabitsRepository by lazy {
        HabitsRepository(applicationContext)
    }
}
