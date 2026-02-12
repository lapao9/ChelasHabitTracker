package pt.isel.pdm.chatr

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
import pt.isel.pdm.chatr.ui.statistics.StatisticsScreen
import pt.isel.pdm.chatr.ui.statistics.StatisticsViewModel
import pt.isel.pdm.chatr.ui.statistics.StatisticsViewModelFactory
import pt.isel.pdm.chatr.ui.theme.CHaTrTheme

/**
 * Activity para exibir as estatísticas dos hábitos do usuário.
 * É iniciada a partir da HabitsActivity quando o usuário clica no ícone de estatísticas.
 */
class StatisticsActivity : ComponentActivity() {
    
    private val viewModel: StatisticsViewModel by viewModels {
        val repository = (application as CHaTrApplication).habitsRepository
        StatisticsViewModelFactory(repository)
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
                    
                    StatisticsScreen(
                        uiState = uiState,
                        onNavigateBack = ::finish
                    )
                }
            }
        }
    }
}
