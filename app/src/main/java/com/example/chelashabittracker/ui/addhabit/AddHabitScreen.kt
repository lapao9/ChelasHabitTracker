package pt.isel.pdm.chatr.ui.addhabit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Screen para adicionar um novo hábito
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    uiState: AddHabitUiState, // Estado da UI
    onNameChange: (String) -> Unit, // nome do hábito alterado
    onDescriptionChange: (String) -> Unit, // descrição do hábito alterada
    onTimesPerDayChange: (String) -> Unit, // número de vezes por dia alterado
    onSaveClick: () -> Unit, // usuário clicou em guardar
    onNavigateBack: () -> Unit, // usuário quer voltar para a tela anterior
    modifier: Modifier = Modifier
) {
    // Navigate back when save is successful
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Hábito") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("Nome do Hábito") },
                placeholder = { Text("Ex: Hidratação") },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descrição (opcional)") },
                placeholder = { Text("Ex: Beber 2L de água") },
                enabled = !uiState.isSaving,
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.timesPerDay,
                onValueChange = onTimesPerDayChange,
                label = { Text("Vezes por dia") },
                placeholder = { Text("Ex: 10") },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onSaveClick,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Hábito")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddHabitScreenPreview() {
    AddHabitScreen(
        uiState = AddHabitUiState(
            name = "Hidratação",
            description = "Beber 2L de água",
            timesPerDay = "10",
            isSaving = false,
            saveSuccess = false
        ),
        onNameChange = {},
        onDescriptionChange = {},
        onTimesPerDayChange = {},
        onSaveClick = {},
        onNavigateBack = {}
    )
}