package pt.isel.pdm.chatr

import android.app.Application
import pt.isel.pdm.chatr.data.HabitsRepository

/**
 * Aplicação personalizada para o Chatra Habit Tracker, que fornece acesso ao repositório de hábitos.
 * Esta classe é definida no AndroidManifest.xml para ser usada como a classe Application da app.
 */
class CHaTrApplication : Application() {
    
    val habitsRepository: HabitsRepository by lazy {
        HabitsRepository(applicationContext)
    }
}
