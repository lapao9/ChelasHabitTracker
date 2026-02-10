package pt.isel.pdm.chatr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pt.isel.pdm.chatr.data.HabitsRepository
import pt.isel.pdm.chatr.ui.addhabit.AddHabitScreen
import pt.isel.pdm.chatr.ui.addhabit.AddHabitViewModel
import pt.isel.pdm.chatr.ui.addhabit.AddHabitViewModelFactory
import pt.isel.pdm.chatr.ui.habits.HabitsScreen
import pt.isel.pdm.chatr.ui.habits.HabitsViewModel
import pt.isel.pdm.chatr.ui.habits.HabitsViewModelFactory
import pt.isel.pdm.chatr.ui.statistics.StatisticsScreen
import pt.isel.pdm.chatr.ui.statistics.StatisticsViewModel
import pt.isel.pdm.chatr.ui.statistics.StatisticsViewModelFactory

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object Habits : Screen("habits")
    data object AddHabit : Screen("add_habit")
    data object Statistics : Screen("statistics")
}

/**
 * Main navigation host for the app.
 * Sets up navigation between the three main screens.
 */
@Composable
fun CHaTrNavHost(
    navController: NavHostController,
    repository: HabitsRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Habits.route,
        modifier = modifier
    ) {
        composable(Screen.Habits.route) {
            val viewModel: HabitsViewModel = viewModel(
                factory = HabitsViewModelFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsState()
            
            HabitsScreen(
                uiState = uiState,
                onAddHabitClick = { navController.navigate(Screen.AddHabit.route) },
                onStatisticsClick = { navController.navigate(Screen.Statistics.route) },
                onRecordCompletion = viewModel::recordCompletion,
                onDeleteHabit = viewModel::deleteHabit
            )
        }
        
        composable(Screen.AddHabit.route) {
            val viewModel: AddHabitViewModel = viewModel(
                factory = AddHabitViewModelFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsState()
            
            AddHabitScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onTimesPerDayChange = viewModel::onTimesPerDayChange,
                onSaveClick = viewModel::saveHabit,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Statistics.route) {
            val viewModel: StatisticsViewModel = viewModel(
                factory = StatisticsViewModelFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsState()
            
            StatisticsScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
