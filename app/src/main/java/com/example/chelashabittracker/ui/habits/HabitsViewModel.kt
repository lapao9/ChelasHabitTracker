package pt.isel.pdm.chatr.ui.habits

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
 * UI state for the habits screen.
 * Represents the state of the habits list and their daily progress.
 */
data class HabitsUiState(
    val habits: List<Habit> = emptyList(),
    val completions: Map<String, Int> = emptyMap(), // habitId -> completedTimes today
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing the habits list and tracking daily completions.
 * Follows MVVM architecture pattern taught in PDM course.
 */
class HabitsViewModel(
    private val repository: HabitsRepository
) : ViewModel() {
    
    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    /**
     * UI state exposed as StateFlow for Compose to observe.
     * Combines habits and today's completions into a single state.
     */
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
     * Records a completion for the specified habit.
     * Launches a coroutine in the ViewModel scope.
     */
    fun recordCompletion(habitId: String) {
        viewModelScope.launch {
            repository.recordCompletion(habitId, today)
        }
    }
    
    /**
     * Deletes the specified habit and all its completions.
     */
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}

/**
 * Factory for creating HabitsViewModel instances.
 * Required because ViewModel has constructor parameters.
 */
class HabitsViewModelFactory(
    private val repository: HabitsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java)) {
            return HabitsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
