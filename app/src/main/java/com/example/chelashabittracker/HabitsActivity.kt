package pt.isel.pdm.chatr

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import pt.isel.pdm.chatr.ui.habits.HabitsScreen
import pt.isel.pdm.chatr.ui.habits.HabitsViewModel
import pt.isel.pdm.chatr.ui.habits.HabitsViewModelFactory
import pt.isel.pdm.chatr.ui.theme.CHaTrTheme

/**
 * Main Activity - mostra a lista de hábitos e permite navegar para as telas de adicionar hábito e estatísticas.
 * Permite também registrar a conclusão de um hábito e remover um hábito existente.
 */
class HabitsActivity : ComponentActivity() {
    
    private val viewModel: HabitsViewModel by viewModels {
        val repository = (application as CHaTrApplication).habitsRepository
        HabitsViewModelFactory(repository)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CHaTrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()
                    
                    HabitsScreen(
                        uiState = uiState,
                        onAddHabitClick = ::navigateToAddHabit,
                        onStatisticsClick = ::navigateToStatistics,
                        onRecordCompletion = viewModel::recordCompletion,
                        onDeleteHabit = viewModel::deleteHabit
                    )
                }
            }
        }
    }
    
    private fun navigateToAddHabit() {
        val intent = Intent(this, AddHabitActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToStatistics() {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }
}
