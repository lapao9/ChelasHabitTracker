# CHaTr - Chelas Habit Tracker

Android application for daily habit tracking, developed for the Mobile Device Programming (PDM) course at ISEL.

## Demo Video

[HERE](https://drive.google.com/file/d/1W6QuBm6nNHAQo5ZHXAXTI5yCMPo3rQxK/view?usp=sharing)

## About the App

CHaTr (Chelas Habit Tracker) is an Android application that allows users to:

- **Define daily habits** – Create habits with a name, description, and a target number of completions per day
- **Log completions** – Track the daily progress of each habit
- **View statistics** – Check habit completion data for the last 7 days

## Project Highlights

**3 independent Activities** – Traditional Android approach with full isolation
**Jetpack Compose** – Modern and reactive UI construction
**DataStore** – Modern and efficient local storage
**Kotlin Coroutines** – Non-blocking operations
**Material Design 3** – Modern and accessible interface
**Visual statistics** – Weekly progress with motivational feedback

### Usage Examples

- **Hydration**: "Drink 2L of Water" – 10 times per day (10 glasses of 20cl)
- **Walking**: "Brisk Walking" – 1 time per day
- **Reading**: "Read 30 minutes" – 2 times per day

---

## Architecture

### Activity Structure

This version implements the **traditional Android approach** with:

1. **HabitsActivity** – Main activity (main/launcher)

   - List of habits
   - Completion logging
   - Navigation to other activities
2. **AddHabitActivity** – Add habit activity

   - Creation form
   - Input validation
3. **StatisticsActivity** – Statistics activity

   - Visual overview of the last 7 days

### Navigation Between Activities

Navigation is done using **explicit Intents**:

```kotlin
// From HabitsActivity to AddHabitActivity
private fun navigateToAddHabit() {
    val intent = Intent(this, AddHabitActivity::class.java)
    startActivity(intent)
}

// From HabitsActivity to StatisticsActivity
private fun navigateToStatistics() {
    val intent = Intent(this, StatisticsActivity::class.java)
    startActivity(intent)
}

// Go back (in child activities)
onNavigateBack = ::finish
```

---

## Features

### Habits Screen (HabitsActivity)

- List of all defined habits
- Daily progress visualization (completed / target)
- Button to log a habit completion
- Option to delete habits
- Visual progress bar indicator
- Button to add new habits
- Navigation to statistics

### Add Habit Screen (AddHabitActivity)

- Form to create new habits
- Required field validation
- Fields: name, description (optional), and times per day
- Back button

### Statistics Screen (StatisticsActivity)

- Data from the last 7 days for each habit
- Number of days the goal was fully met
- Total number of completions logged
- Performance-based motivational messages
- Visual weekly progress indicator
- Back button

---

## Architecture & Technologies

### MVVM Architecture (Model-View-ViewModel)

- **Model**: Data layer with `Habit` and `HabitCompletion`
- **View**: UI implemented in Jetpack Compose (per Activity)
- **ViewModel**: State management and business logic (one per Activity)

### Technologies Used

#### UI

- **Jetpack Compose** – Declarative UI framework
- **Material Design 3** – Modern design system
- **Multiple Activities** – One Activity per screen

#### State Management

- **StateFlow** – Reactive state flows
- **ViewModel** – State preservation across configuration changes
- **State Hoisting** – Separation between state and UI

#### Data Persistence

- **DataStore** – Persistent data storage
- **Kotlinx Serialization** – JSON serialization/deserialization

#### Concurrency

- **Kotlin Coroutines** – Asynchronous operations
- **ViewModelScope** – Coroutine lifecycle management

#### Navigation

- **Explicit Intents** – Navigation between activities
- **finish()** – Return to the previous activity

---

## Project Structure

```
app/src/main/java/pt/isel/pdm/chatr/
├── HabitsActivity.kt            # Main activity (launcher) – Habits list
├── AddHabitActivity.kt          # Activity for adding new habits
├── StatisticsActivity.kt        # Activity for viewing statistics
├── CHaTrApplication.kt          # Application class (Service Locator)
├── data/
│   └── HabitsRepository.kt      # Repository for data access (DataStore)
├── domain/
│   ├── Habit.kt                 # Data model: Habit
│   └── HabitCompletion.kt       # Data model: Completion record
└── ui/
    ├── habits/
    │   ├── HabitsScreen.kt      # Habits screen composable
    │   └── HabitsViewModel.kt   # Habits list ViewModel
    ├── addhabit/
    │   ├── AddHabitScreen.kt    # Add habit screen composable
    │   └── AddHabitViewModel.kt # Add habit form ViewModel
    ├── statistics/
    │   ├── StatisticsScreen.kt      # Statistics screen composable
    │   └── StatisticsViewModel.kt   # Statistics ViewModel
    └── theme/
        ├── Color.kt             # Material 3 color definitions
        ├── Theme.kt             # Theme configuration
        └── Type.kt              # Typography definitions
```

> **Architecture note**: Each Activity is responsible for:
>
> 1. Initializing its own ViewModel (using `viewModels()`)
> 2. Setting up the Compose UI with the app theme
> 3. Passing navigation callbacks to the Composable
> 4. Managing navigation through explicit Intents

---

## Code Examples

### Navigation Between Activities

**HabitsActivity.kt** – Main activity with navigation:

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
                    onAddHabitClick = ::navigateToAddHabit,      // Navigation callback
                    onStatisticsClick = ::navigateToStatistics,  // Navigation callback
                    onRecordCompletion = viewModel::recordCompletion,
                    onDeleteHabit = viewModel::deleteHabit
                )
            }
        }
    }

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

**AddHabitActivity.kt** – Activity with back navigation:

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
                    onNavigateBack = ::finish  // Closes the activity and goes back
                )
            }
        }
    }
}
```

### Navigation Flow

```
┌─────────────────────────┐
│   HabitsActivity        │ Launcher Activity
│   (Habits List)         │
└─────────────────────────┘
          │
          ├──────────────────────────────────┐
          │                                  │
          ↓                                  ↓
┌─────────────────────────┐    ┌─────────────────────────┐
│  AddHabitActivity       │    │  StatisticsActivity     │
│  (Add Habit)            │    │  (View Statistics)      │
└─────────────────────────┘    └─────────────────────────┘
          │                                  │
          │ finish()                         │ finish()
          ↓                                  ↓
┌─────────────────────────┐
│   HabitsActivity        │ ← Returns automatically
└─────────────────────────┘
```

---

## AndroidManifest.xml

```xml
<application>
    <!-- Main Activity – Habits List (Launcher) -->
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

---

## Technical Decisions

### Why DataStore?

1. **Fit for the Use Case**:

   - Simple structured data (list of habits and completion records)
   - No need for complex queries
   - Relatively small data size
2. **Simplicity**:

   - Straightforward and intuitive to use
   - Easier to implement compared to relational databases
   - No server setup required (unlike Firestore)
3. **Performance**:

   - Fast data access
   - Asynchronous operations with Coroutines
   - Native Flow support for reactive observation

### Alternatives Considered and Rejected

| Solution               | Advantages                              | Disadvantages                                     | Fit for CHaTr    |
| ---------------------- | --------------------------------------- | ------------------------------------------------- | ---------------- |
| **DataStore** ✅ | Simple, Asynchronous                    | No complex query support                          | **CHOSEN** |
| **Firestore**    | Cloud sync, Real-time updates, Scalable | Requires internet, Firebase setup, Non-local data | Unnecessary      |

**Conclusion**: DataStore is the perfect fit for this use case – simple enough to avoid unnecessary complexity, yet robust and modern enough for a professional project.

---

## Data Architecture

### Application Data Flow

```
┌─────────────────────────────────────────────────────────┐
│                      CHaTrApplication                    │
│                    (Service Locator)                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │          habitsRepository: HabitsRepository       │  │
│  └───────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────┘
                        │ Provides Repository
        ┌───────────────┼───────────────────┐
        │               │                   │
        ↓               ↓                   ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Habits     │ │   AddHabit   │ │  Statistics  │
│  Activity    │ │   Activity   │ │   Activity   │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │
       │ creates        │ creates        │ creates
       ↓                ↓                ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Habits     │ │   AddHabit   │ │  Statistics  │
│  ViewModel   │ │  ViewModel   │ │  ViewModel   │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │
       │ uses           │ uses           │ uses
       └────────────────┴────────────────┘
                        │
                        ↓
               ┌────────────────┐
               │    Habits      │
               │   Repository   │
               └────────┬───────┘
                        │
                        │ reads/writes
                        ↓
               ┌────────────────┐
               │   DataStore    │
               │  (Preferences) │
               └────────────────┘
                        │
                        ↓
                  Local file
         (chatr_prefs.preferences_pb)
```

### Data Models

**Habit** – Represents a habit to be tracked:

```kotlin
@Serializable
data class Habit(
    val id: String,              // Unique UUID
    val name: String,            // e.g. "Hydration"
    val description: String,     // e.g. "Drink 2L of water"
    val timesPerDay: Int         // e.g. 10 (times per day)
)
```

**HabitCompletion** – Completion record for a habit:

```kotlin
@Serializable
data class HabitCompletion(
    val habitId: String,         // Reference to the habit
    val date: String,            // "2026-02-10" (ISO format)
    val completedTimes: Int      // How many times it was done on this day
)
```

---

## Author

**Name**: João Lapão
**Student Number**: A51542
**Degree**: Computer Engineering, Networks and Telecommunications
**Institution**: Instituto Superior de Engenharia de Lisboa (ISEL)

## Assignment Info

- **Course**: Mobile Device Programming (PDM)
- **Academic Year**: 2025/2026
- **Semester**: Winter
- **Submission Date**: February 2026

---

**ISEL – Instituto Superior de Engenharia de Lisboa**
