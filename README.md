# CHaTr - Chelas Habit Tracker

Aplicação Android para monitorização de hábitos diários desenvolvida no âmbito da disciplina de Programação em Dispositivos Móveis (PDM) do ISEL.

## Vídeo de Demostração: 

## Sobre a Aplicação

CHaTr (Chelas Habit Tracker) é uma aplicação Android que permite aos utilizadores:

- **Definir hábitos diários** - Criar hábitos com nome, descrição e número de vezes que devem ser realizados por dia
- **Registar realizações** - Acompanhar o progresso diário de cada hábito
- **Visualizar estatísticas** - Consultar informação sobre o cumprimento dos hábitos nos últimos 7 dias

## Destaques do Projeto

**3 Activities independentes** - Abordagem tradicional do Android com isolamento completo  
**Jetpack Compose** - Construção de UI moderna e reativa
**DataStore** - Armazenamento local moderno e eficiente
**Kotlin Coroutines** - Operações não-bloqueantes  
**Material Design 3** - Interface moderna e acessível  
**Estatísticas visuais** - Progresso semanal com motivação  

### Exemplos de Uso

- **Hidratação**: "Beber 2L de Água" - 10 vezes por dia (10 copos de 20cl)
- **Caminhada**: "Caminhada em Ritmo Acelerado" - 1 vez por dia
- **Leitura**: "Ler 30 minutos" - 2 vezes por dia


### Estrutura de Activities

Esta versão implementa a abordagem **tradicional do Android** com:

1. **HabitsActivity** - Activity principal (main/launcher)
   - Lista de hábitos
   - Registo de completudes
   - Navegação para outras activities

2. **AddHabitActivity** - Activity para adicionar hábito
   - Formulário de criação
   - Validação de inputs

3. **StatisticsActivity** - Activity para estatísticas
   - Visualização dos últimos 7 dias

### Navegação entre Activities

A navegação é feita através de **Intents explícitos**:

```kotlin
// De HabitsActivity para AddHabitActivity
private fun navigateToAddHabit() {
    val intent = Intent(this, AddHabitActivity::class.java)
    startActivity(intent)
}

// De HabitsActivity para StatisticsActivity
private fun navigateToStatistics() {
    val intent = Intent(this, StatisticsActivity::class.java)
    startActivity(intent)
}

// Voltar atrás (nas activities filhas)
onNavigateBack = ::finish
```

## Funcionalidades

### Ecrã de Hábitos (HabitsActivity)
- Lista de todos os hábitos definidos
- Visualização do progresso diário (completado/objetivo)
- Botão para registar a realização de um hábito
- Opção para eliminar hábitos
- Indicador visual de progresso com barra de progressão
- Botão para adicionar novos hábitos
- Navegação para estatísticas

### Ecrã de Adicionar Hábito (AddHabitActivity)
- Formulário para criar novos hábitos
- Validação de campos obrigatórios
- Campos: nome, descrição (opcional) e o número de vezes por dia
- Botão "voltar" 

### Ecrã de Estatísticas (StatisticsActivity)
- Informação dos últimos 7 dias para cada hábito
- Número de dias em que o objetivo foi totalmente cumprido
- Total de registos realizados
- Mensagens baseadas no desempenho
- Indicador visual de progresso semanal
- Botão "voltar"

## Arquitetura e Tecnologias

### Arquitetura MVVM (Model-View-ViewModel)
- **Model**: Camada de dados com `Habit` e `HabitCompletion`
- **View**: UI implementada em Jetpack Compose (em cada Activity)
- **ViewModel**: Gestão de estado e lógica de negócio (um por Activity)

### Tecnologias Utilizadas

#### UI
- **Jetpack Compose** - Framework declarativo para construção de UI
- **Material Design 3** - Sistema de design moderno
- **Multiple Activities** - Uma Activity por ecrã

#### Gestão de Estado
- **StateFlow** - Fluxos de estado reativo
- **ViewModel** - Preservação de estado durante mudanças de configuração
- **State Hoisting** - Separação entre estado e UI

#### Persistência de Dados
- **DataStore** - Armazenamento persistente de dados
- **Kotlinx Serialization** - Serialização/deserialização JSON

#### Concorrência
- **Kotlin Coroutines** - Operações assíncronas
- **ViewModelScope** - Gestão do ciclo de vida de coroutines

#### Navegação
- **Explicit Intents** - Navegação entre Activities
- **finish()** - Voltar para a Activity anterior

### Estrutura do Projeto

```
app/src/main/java/pt/isel/pdm/chatr/
├── HabitsActivity.kt            # Activity principal (launcher) - Lista de hábitos
├── AddHabitActivity.kt          # Activity para adicionar novos hábitos
├── StatisticsActivity.kt        # Activity para visualizar estatísticas
├── CHaTrApplication.kt          # Application class (Service Locator)
├── data/
│   └── HabitsRepository.kt      # Repository para acesso aos dados (DataStore)
├── domain/
│   ├── Habit.kt                 # Modelo de dados: Hábito
│   └── HabitCompletion.kt       # Modelo de dados: Registo de conclusão
└── ui/
    ├── habits/
    │   ├── HabitsScreen.kt      # Composable do ecrã de hábitos
    │   └── HabitsViewModel.kt   # ViewModel da lista de hábitos
    ├── addhabit/
    │   ├── AddHabitScreen.kt    # Composable do ecrã de adicionar
    │   └── AddHabitViewModel.kt # ViewModel do formulário
    ├── statistics/
    │   ├── StatisticsScreen.kt      # Composable do ecrã de estatísticas
    │   └── StatisticsViewModel.kt   # ViewModel das estatísticas
    └── theme/
        ├── Color.kt             # Definição de cores Material 3
        ├── Theme.kt             # Configuração do tema
        └── Type.kt              # Definição de tipografia
```

**Nota sobre a arquitetura**: Cada Activity é responsável por:
1. Inicializar o seu ViewModel (usando `viewModels()`)
2. Configurar o Compose UI com o tema
3. Passar callbacks de navegação para o Composable
4. Gerir a navegação através de Intents explícitos


### AndroidManifest.xml

```xml
   <application >
       <!-- Main Activity - Habits List (Launcher) -->
       <activity
           android:name=".HabitsActivity"
           android:exported="true">
           <intent-filter>
               <action android:name="android.intent.action.MAIN" />
               <category android:name="android.intent.category.LAUNCHER" />
           </intent-filter>
       </activity>
       
       <!-- Add Habit Activity -->
       <activity
           android:name=".AddHabitActivity"
           android:parentActivityName=".HabitsActivity" />
       
       <!-- Statistics Activity -->
       <activity
           android:name=".StatisticsActivity"
           android:parentActivityName=".HabitsActivity" />
   </application>
```

## Exemplos de Código

### Navegação Entre Activities

**HabitsActivity.kt** - Activity principal com navegação:
```kotlin
class HabitsActivity : ComponentActivity() {
    private val viewModel: HabitsViewModel by viewModels {
        HabitsViewModelFactory((application as CHaTrApplication).habitsRepository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CHaTrTheme {
                val uiState by viewModel.uiState.collectAsState()
                HabitsScreen(
                    uiState = uiState,
                    onAddHabitClick = ::navigateToAddHabit,      // <-- Callback de navegação
                    onStatisticsClick = ::navigateToStatistics,  // <-- Callback de navegação
                    onRecordCompletion = viewModel::recordCompletion,
                    onDeleteHabit = viewModel::deleteHabit
                )
            }
        }
    }
    
    // Navegação explícita com Intents
    private fun navigateToAddHabit() {
        val intent = Intent(this, AddHabitActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToStatistics() {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }
}
```

**AddHabitActivity.kt** - Activity com navegação de retorno:
```kotlin
class AddHabitActivity : ComponentActivity() {
    private val viewModel: AddHabitViewModel by viewModels {
        AddHabitViewModelFactory((application as CHaTrApplication).habitsRepository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CHaTrTheme {
                val uiState by viewModel.uiState.collectAsState()
                AddHabitScreen(
                    uiState = uiState,
                    onNameChange = viewModel::onNameChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onTimesPerDayChange = viewModel::onTimesPerDayChange,
                    onSaveClick = viewModel::saveHabit,
                    onNavigateBack = ::finish  // <-- Fecha a Activity e volta atrás
                )
            }
        }
    }
}
```

### Fluxo de Navegação

```
┌─────────────────────────┐
│   HabitsActivity        │ Launcher Activity
│   (Lista de hábitos)    │
└─────────────────────────┘
          │
          ├──────────────────────────────────┐
          │                                  │
          ↓                                  ↓
┌─────────────────────────┐    ┌─────────────────────────┐
│  AddHabitActivity       │    │  StatisticsActivity     │
│  (Adicionar hábito)     │    │  (Ver estatísticas)     │
└─────────────────────────┘    └─────────────────────────┘
          │                                  │
          │ finish()                         │ finish()
          ↓                                  ↓
┌─────────────────────────┐
│   HabitsActivity        │ ← Volta automaticamente
└─────────────────────────┘
```

## Decisões Técnicas

### Porquê DataStore?

1. **Adequação ao Caso de Uso**: 
   - Dados estruturados simples (lista de hábitos e registos)
   - Não requer queries complexas
   - Tamanho de dados relativamente pequeno

2. **Simplicidade**:
   - Simples e intuitivo
   - Mais fácil de implementar comparado com bases de dados relacionais
   - Não requer configuração de servidor (contrariamente a Firestore)

3. **Performance**:
   - Acesso rápido aos dados
   - Operações assíncronas com Coroutines
   - Suporte nativo a Flow para observação reativa

### Alternativas Consideradas e Rejeitadas

| Solução | Vantagens | Desvantagens | Adequação para CHaTr |
|---------|-----------|--------------|----------------------|
| **DataStore** ✅ | • Simples<br> • Assíncrono | • Não suporta queries complexas |**ESCOLHIDO** |
| **Firestore** | • Cloud sync<br>• Real-time updates<br>• Escalável | • Requer internet<br>• Setup Firebase<br>• Dados não locais | Desnecessário |

**Conclusão**: DataStore é a escolha perfeita para este caso de uso - simples o suficiente para não adicionar complexidade desnecessária, mas robusto e moderno o suficiente para um projeto profissional.

## 📊 Arquitetura de Dados

### Fluxo de Dados na Aplicação

```
┌─────────────────────────────────────────────────────────┐
│                      CHaTrApplication                    │
│                    (Service Locator)                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │          habitsRepository: HabitsRepository       │  │
│  └───────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────┘
                            │ Fornece Repository
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ↓                   ↓                   ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Habits     │   │   AddHabit   │   │  Statistics  │
│  Activity    │   │   Activity   │   │   Activity   │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       │ cria             │ cria             │ cria
       ↓                  ↓                  ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Habits     │   │   AddHabit   │   │  Statistics  │
│  ViewModel   │   │  ViewModel   │   │  ViewModel   │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       │ usa              │ usa              │ usa
       ↓                  ↓                  ↓
       └──────────────────┴──────────────────┘
                          │
                          ↓
                 ┌────────────────┐
                 │    Habits      │
                 │   Repository   │
                 └────────┬───────┘
                          │
                          │ lê/escreve
                          ↓
                 ┌────────────────┐
                 │   DataStore    │
                 │  (Preferences) │
                 └────────────────┘
                          │
                          ↓
                    Ficheiro local
             (chatr_prefs.preferences_pb)
```

### Modelos de Dados

**Habit** - Representa um hábito a ser acompanhado:
```kotlin
@Serializable
data class Habit(
    val id: String,              // UUID único
    val name: String,            // Ex: "Hidratação"
    val description: String,     // Ex: "Beber 2L de água"
    val timesPerDay: Int         // Ex: 10 (vezes por dia)
)
```

**HabitCompletion** - Registo de conclusão de um hábito:
```kotlin
@Serializable
data class HabitCompletion(
    val habitId: String,         // Referência ao hábito
    val date: String,            // "2026-02-10" (ISO format)
    val completedTimes: Int      // Quantas vezes foi realizado neste dia
)
```
## Autor

**Nome**: João Lapão

**Número**: A51542

**Curso**: Engenharia Informática, Redes e Telecomunicações

**Instituição**: Instituto Superior de Engenharia de Lisboa (ISEL)

## Informações do Trabalho

- **Disciplina**: Programação em Dispositivos Móveis (PDM)
- **Ano Letivo**: 2025/2026
- **Semestre**: Inverno
- **Data de Entrega**: Fevereiro de 2026

---

**ISEL - Instituto Superior de Engenharia de Lisboa**
