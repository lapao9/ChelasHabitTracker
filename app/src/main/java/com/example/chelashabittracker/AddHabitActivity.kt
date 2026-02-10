package pt.isel.pdm.chatr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import pt.isel.pdm.chatr.ui.addhabit.AddHabitScreen
import pt.isel.pdm.chatr.ui.addhabit.AddHabitViewModel
import pt.isel.pdm.chatr.ui.addhabit.AddHabitViewModelFactory
import pt.isel.pdm.chatr.ui.theme.CHaTrTheme

/**
 * Activity for adding a new habit.
 * Launched from HabitsActivity when user clicks the "Add" button.
 */
class AddHabitActivity : ComponentActivity() {
    
    private val viewModel: AddHabitViewModel by viewModels {
        val repository = (application as CHaTrApplication).habitsRepository
        AddHabitViewModelFactory(repository)
    }
    
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
                    
                    AddHabitScreen(
                        uiState = uiState,
                        onNameChange = viewModel::onNameChange,
                        onDescriptionChange = viewModel::onDescriptionChange,
                        onTimesPerDayChange = viewModel::onTimesPerDayChange,
                        onSaveClick = viewModel::saveHabit,
                        onNavigateBack = ::finish
                    )
                }
            }
        }
    }
}
