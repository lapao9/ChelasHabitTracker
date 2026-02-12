package pt.isel.pdm.chatr.ui.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isel.pdm.chatr.domain.Habit

/**
 * Main screen for displaying and tracking habits.
 * This is a stateless composable that receives all data and callbacks as parameters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    uiState: HabitsUiState,
    onAddHabitClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onRecordCompletion: (String) -> Unit,
    onDeleteHabit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CHaTr",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chelas Habit Tracker",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onStatisticsClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Ver estatísticas"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if(!uiState.habits.isEmpty()){
                FloatingActionButton(
                    onClick = onAddHabitClick,
                    containerColor = MaterialTheme.colorScheme.primary,   // Cor de destaque
                    contentColor = MaterialTheme.colorScheme.onPrimary,   // Ícone visível
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    ),
                    modifier = Modifier.size(72.dp) // Maior, mais visível
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar hábito",
                        modifier = Modifier.size(36.dp) // Ícone maior
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.habits.isEmpty()) {
            EmptyHabitsContent(
                onAddHabitClick = onAddHabitClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            HabitsList(
                habits = uiState.habits,
                completions = uiState.completions,
                onRecordCompletion = onRecordCompletion,
                onDeleteHabit = onDeleteHabit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun EmptyHabitsContent(
    onAddHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nenhum hábito ainda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cria o teu primeiro hábito e começa a acompanhar o teu progresso diário.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onAddHabitClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Criar hábito")
        }
    }
}

@Composable
private fun HabitsList(
    habits: List<Habit>,
    completions: Map<String, Int>,
    onRecordCompletion: (String) -> Unit,
    onDeleteHabit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                completedTimes = completions[habit.id] ?: 0,
                onRecordCompletion = { onRecordCompletion(habit.id) },
                onDeleteHabit = { onDeleteHabit(habit.id) }
            )
        }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    completedTimes: Int,
    onRecordCompletion: () -> Unit,
    onDeleteHabit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar hábito",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedTimes / ${habit.timesPerDay}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (completedTimes >= habit.timesPerDay) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Button(
                    onClick = onRecordCompletion,
                    enabled = completedTimes < habit.timesPerDay
                ) {
                    Text(
                        if (completedTimes < habit.timesPerDay) "Registar" else "Concluído"
                    )
                }
            }

            LinearProgressIndicator(
                progress = { (completedTimes.toFloat() / habit.timesPerDay.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar hábito?") },
            text = { Text("Tem a certeza que quer eliminar o hábito \"${habit.name}\"? Esta ação não pode ser revertida.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteHabit()
                        showDeleteDialog = false

                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HabitsScreenPreview() {
    val sampleHabits = listOf(
        Habit(id = "1", name = "Hidratação", description = "Beber 2L de água", timesPerDay = 10),
        Habit(id = "2", name = "Exercício", description = "30 minutos de caminhada", timesPerDay = 1)
    )
    val sampleCompletions = mapOf(
        "1" to 3,
        "2" to 1
    )

    HabitsScreen(
        uiState = HabitsUiState(
            habits = sampleHabits,
            completions = sampleCompletions,
            isLoading = false
        ),
        onAddHabitClick = {},
        onStatisticsClick = {},
        onRecordCompletion = {},
        onDeleteHabit = {}
    )
}