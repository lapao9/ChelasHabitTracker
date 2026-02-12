package pt.isel.pdm.chatr.ui.habits

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.isel.pdm.chatr.data.HabitsRepository
import pt.isel.pdm.chatr.domain.Habit
import pt.isel.pdm.chatr.domain.HabitCompletion
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Estado UI para a tela de hábitos
 */
data class HabitsUiState(
    val habits: List<Habit> = emptyList(),
    val completions: Map<String, Int> = emptyMap(), // habitId -> completedTimes today
    val isLoading: Boolean = true
)

/**
 * ViewModel para gerenciar a tela de hábitos
 * Segue padrão MVVM
 */
class HabitsViewModel(
    private val repository: HabitsRepository
) : ViewModel() {
    
    @RequiresApi(Build.VERSION_CODES.O) //Android Studio pediu
    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    /**
     * estado UI que combina os hábitos e as conclusões de hoje.
     * Usa combine para reagir a mudanças em ambos os Flows do repositório
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HabitsUiState> = combine(
        repository.habits,
        repository.completions
    ) { habits, allCompletions ->
        // Filter completions for today
        val todayCompletions = allCompletions
            .filter { it.date == today }
            .associate { it.habitId to it.completedTimes }
        
        HabitsUiState(
            habits = habits,
            completions = todayCompletions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitsUiState()
    )
    
    /**
     * Regista uma conclusão para o hábito especificado
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun recordCompletion(habitId: String) {
        viewModelScope.launch {
            repository.recordCompletion(habitId, today)
        }
    }
    
    /**
     * Remove o hábito especificado do repositório
     */
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}

/**
 * Factory para criar instâncias de HabitsViewModel
 * É preciso porque o ViewModel tem parâmetros no construtor
 */
class HabitsViewModelFactory(
    private val repository: HabitsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HabitsViewModel::class.java) -> {
                HabitsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }    }
}
