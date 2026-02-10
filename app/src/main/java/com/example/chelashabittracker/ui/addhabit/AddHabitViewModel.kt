package pt.isel.pdm.chatr.ui.addhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isel.pdm.chatr.data.HabitsRepository
import pt.isel.pdm.chatr.domain.Habit
import java.util.UUID

/**
 * UI state for adding a new habit.
 * Represents the form state and validation.
 */
data class AddHabitUiState(
    val name: String = "",
    val description: String = "",
    val timesPerDay: String = "1",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val nameError: String? = null,
    val timesPerDayError: String? = null
)

/**
 * ViewModel for managing the add habit form.
 */
class AddHabitViewModel(
    private val repository: HabitsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()
    
    /**
     * Updates the habit name in the form.
     */
    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = null
        )
    }
    
    /**
     * Updates the habit description in the form.
     */
    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    /**
     * Updates the times per day in the form.
     */
    fun onTimesPerDayChange(timesPerDay: String) {
        _uiState.value = _uiState.value.copy(
            timesPerDay = timesPerDay,
            timesPerDayError = null
        )
    }
    
    /**
     * Validates and saves the habit.
     * Returns true if validation passes, false otherwise.
     */
    fun saveHabit() {
        val currentState = _uiState.value
        
        // Validate inputs
        val nameError = when {
            currentState.name.isBlank() -> "O nome é obrigatório"
            else -> null
        }
        
        val timesPerDayError = when {
            currentState.timesPerDay.isBlank() -> "Número de vezes é obrigatório"
            currentState.timesPerDay.toIntOrNull() == null -> "Deve ser um número válido"
            currentState.timesPerDay.toInt() < 1 -> "Deve ser pelo menos 1"
            else -> null
        }
        
        if (nameError != null || timesPerDayError != null) {
            _uiState.value = currentState.copy(
                nameError = nameError,
                timesPerDayError = timesPerDayError
            )
            return
        }
        
        // Save habit
        _uiState.value = currentState.copy(isSaving = true)
        
        viewModelScope.launch {
            val habit = Habit(
                id = UUID.randomUUID().toString(),
                name = currentState.name,
                description = currentState.description,
                timesPerDay = currentState.timesPerDay.toInt()
            )
            
            repository.addHabit(habit)
            
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveSuccess = true
            )
        }
    }
    
    /**
     * Resets the save success flag.
     */
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}

/**
 * Factory for creating AddHabitViewModel instances.
 */
class AddHabitViewModelFactory(
    private val repository: HabitsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddHabitViewModel::class.java)) {
            return AddHabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
