package pt.isel.pdm.chatr.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pt.isel.pdm.chatr.domain.Habit
import pt.isel.pdm.chatr.domain.HabitCompletion

// Extension property para criar o DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chatr_prefs")

/**
 * Repository for managing habits and their completions using DataStore.
 * Follows the Repository pattern taught in PDM course.
 */
class HabitsRepository(private val context: Context) {
    
    private val habitsKey = stringPreferencesKey("habits")
    private val completionsKey = stringPreferencesKey("completions")
    
    /**
     * Flow of all habits stored in DataStore.
     */
    val habits: Flow<List<Habit>> = context.dataStore.data.map { preferences ->
        val habitsJson = preferences[habitsKey] ?: "[]"
        Json.decodeFromString<List<Habit>>(habitsJson)
    }
    
    /**
     * Flow of all habit completions stored in DataStore.
     */
    val completions: Flow<List<HabitCompletion>> = context.dataStore.data.map { preferences ->
        val completionsJson = preferences[completionsKey] ?: "[]"
        Json.decodeFromString<List<HabitCompletion>>(completionsJson)
    }
    
    /**
     * Adds a new habit to the repository.
     */
    suspend fun addHabit(habit: Habit) {
        context.dataStore.edit { preferences ->
            val currentHabits = preferences[habitsKey]?.let {
                Json.decodeFromString<List<Habit>>(it)
            } ?: emptyList()
            
            val updatedHabits = currentHabits + habit
            preferences[habitsKey] = Json.encodeToString(updatedHabits)
        }
    }
    
    /**
     * Deletes a habit from the repository.
     * Also removes all completions associated with this habit.
     */
    suspend fun deleteHabit(habitId: String) {
        context.dataStore.edit { preferences ->
            // Remove habit
            val currentHabits = preferences[habitsKey]?.let {
                Json.decodeFromString<List<Habit>>(it)
            } ?: emptyList()
            
            val updatedHabits = currentHabits.filter { it.id != habitId }
            preferences[habitsKey] = Json.encodeToString(updatedHabits)
            
            // Remove completions for this habit
            val currentCompletions = preferences[completionsKey]?.let {
                Json.decodeFromString<List<HabitCompletion>>(it)
            } ?: emptyList()
            
            val updatedCompletions = currentCompletions.filter { it.habitId != habitId }
            preferences[completionsKey] = Json.encodeToString(updatedCompletions)
        }
    }
    
    /**
     * Records a completion for a habit on a specific date.
     * If a completion already exists for that habit and date, it increments the count.
     */
    suspend fun recordCompletion(habitId: String, date: String) {
        context.dataStore.edit { preferences ->
            val currentCompletions = preferences[completionsKey]?.let {
                Json.decodeFromString<List<HabitCompletion>>(it)
            } ?: emptyList()
            
            val existingCompletion = currentCompletions.find { 
                it.habitId == habitId && it.date == date 
            }
            
            val updatedCompletions = if (existingCompletion != null) {
                // Increment existing completion
                currentCompletions.map {
                    if (it.habitId == habitId && it.date == date) {
                        it.copy(completedTimes = it.completedTimes + 1)
                    } else {
                        it
                    }
                }
            } else {
                // Add new completion
                currentCompletions + HabitCompletion(habitId, date, 1)
            }
            
            preferences[completionsKey] = Json.encodeToString(updatedCompletions)
        }
    }
}
