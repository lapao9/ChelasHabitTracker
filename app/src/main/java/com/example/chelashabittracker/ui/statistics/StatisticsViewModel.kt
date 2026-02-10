package pt.isel.pdm.chatr.ui.statistics

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
 * Statistics for a single habit.
 */
data class HabitStatistics(
    val habit: Habit,
    val daysCompleted: Int,  // Number of days the goal was fully achieved in the last 7 days
    val totalCompletions: Int  // Total number of completions in the last 7 days
)

/**
 * UI state for the statistics screen.
 */
data class StatisticsUiState(
    val statistics: List<HabitStatistics> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel for displaying habit statistics.
 */
class StatisticsViewModel(
    repository: HabitsRepository
) : ViewModel() {
    
    /**
     * UI state that combines habits and completions to generate statistics.
     */
    val uiState: StateFlow<StatisticsUiState> = combine(
        repository.habits,
        repository.completions
    ) { habits, completions ->
        val today = LocalDate.now()
        val last7Days = (0..6).map { daysAgo ->
            today.minusDays(daysAgo.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
        
        val statistics = habits.map { habit ->
            // Get completions for this habit in the last 7 days
            val habitCompletions = completions.filter { completion ->
                completion.habitId == habit.id && completion.date in last7Days
            }
            
            // Count how many days the goal was fully achieved
            val daysCompleted = habitCompletions.count { completion ->
                completion.completedTimes >= habit.timesPerDay
            }
            
            // Count total completions
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
 * Factory for creating StatisticsViewModel instances.
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
