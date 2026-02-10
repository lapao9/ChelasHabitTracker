package pt.isel.pdm.chatr.domain

import kotlinx.serialization.Serializable

/**
 * Represents the completion record of a habit for a specific date.
 *
 * @property habitId The ID of the habit
 * @property date The date in format "yyyy-MM-dd"
 * @property completedTimes Number of times the habit was completed on this date
 */
@Serializable
data class HabitCompletion(
    val habitId: String,
    val date: String,
    val completedTimes: Int
)
