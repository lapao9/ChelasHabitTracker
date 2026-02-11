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

// criar DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chatr_prefs")

/**
 * Repositório para gerenciar hábitos e suas conclusões usando DataStore
 */
class HabitsRepository(private val context: Context) {

    private val habitsKey : Preferences.Key<String>
                                = stringPreferencesKey("habits")
    private val completionsKey : Preferences.Key<String>
                                = stringPreferencesKey("completions")
    
    /**
     * Flow de todos os hábitos armazenados no DataStore
     * Cada vez que os dados mudarem Flow emite nova lista de hábitos.
     */
    val habits: Flow<List<Habit>> = context.dataStore.data.map { preferences ->
        val habitsJson = preferences[habitsKey] ?: "[]" //"preferences[habitsKey]" retorna um ob
        Json.decodeFromString<List<Habit>>(habitsJson)
    }
    
    /**
     * Flow de todas as conclusões de hábitos armazenadas no DataStore
     * Cada vez que os dados mudarem Flow emite nova lista de conclusões
     */
    val completions: Flow<List<HabitCompletion>> = context.dataStore.data.map { preferences ->
        val completionsJson = preferences[completionsKey] ?: "[]"
        Json.decodeFromString<List<HabitCompletion>>(completionsJson)
    }
    
    /**
     * Adicionar um Hábito
     */
    suspend fun addHabit(habit: Habit) {
        context.dataStore.edit { preferences ->//preferences é do tipo
            val currentHabits = preferences[habitsKey]?.let {
                Json.decodeFromString<List<Habit>>(it)
            } ?: emptyList()
            
            val updatedHabits = currentHabits + habit
            preferences[habitsKey] = Json.encodeToString(updatedHabits)
        }
    }
    
    /**
     * remover hábito
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
     * Registar uma compeltion para um hábito numa data específica
     * Se já existir uma compeltion para esse hábito e data, incrementa a contagem
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
                // Incrementar completion existente
                currentCompletions.map { //o map faz um
                    if (it.habitId == habitId && it.date == date) { //se for o hábito e data que queremos atualizar
                        it.copy(completedTimes = it.completedTimes + 1)
                    } else { //se não for, manter igual
                        it
                    }
                }
            } else { //se não existir completion (1ª vez a registar completação para esse hábito)
                // Add new completion
                currentCompletions + HabitCompletion(habitId, date, 1)
            }
            
            preferences[completionsKey] = Json.encodeToString(updatedCompletions)
        }
    }
}
