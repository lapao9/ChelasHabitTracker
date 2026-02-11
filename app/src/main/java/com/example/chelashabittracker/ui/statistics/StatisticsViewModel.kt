package pt.isel.pdm.chatr.ui.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pt.isel.pdm.chatr.data.HabitsRepository
import pt.isel.pdm.chatr.domain.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Estatísticas de um hábito
 */
data class HabitStatistics(
    val habit: Habit,
    val daysCompleted: Int,  // Númeor de dias em que a meta foi completamente atingida nos últimos 7 dias
    val totalCompletions: Int  // Número total de vezes que o hábito foi completado nos últimos 7 dias
)


data class StatisticsUiState(
    val statistics: List<HabitStatistics> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel para display das estatísticas dos hábitos.
 */
class StatisticsViewModel(
    repository: HabitsRepository
) : ViewModel() {
    
    /**
     * UI state que combina hábitos com completions para calcular as estatísticas dos hábitos nos últimos 7 dias.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<StatisticsUiState> = combine(
        repository.habits, //do tipo Flow<List<Habit>>
        repository.completions
    ) { habits, completions -> //aqui habits é do tipo List<Habit>
        val today = LocalDate.now()
        val last7Days = (0..6).map { daysAgo ->
            today.minusDays(daysAgo.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
        
        val statistics = habits.map { habit ->
            // Get completions para este hábito nos últimos 7 dias
            val habitCompletions = completions.filter { completion ->
                completion.habitId == habit.id && completion.date in last7Days
            }
            
            // Contar quantos dias a meta foi completamente atingida
            val daysCompleted = habitCompletions.count { completion ->
                completion.completedTimes >= habit.timesPerDay
            }
            
            // Contar o número total de vezes que o hábito foi completado nos últimos 7 dias
            val totalCompletions = habitCompletions.sumOf { it.completedTimes }
            
            HabitStatistics(
                habit = habit,
                daysCompleted = daysCompleted,
                totalCompletions = totalCompletions
            )
        }
        
        StatisticsUiState(
            statistics = statistics,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )
}

/**
 * Factory para criar o StatisticsViewModel com dependência do HabitsRepository
 */
class StatisticsViewModelFactory(
    private val repository: HabitsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
