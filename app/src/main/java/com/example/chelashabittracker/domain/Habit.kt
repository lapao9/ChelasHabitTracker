package pt.isel.pdm.chatr.domain

import kotlinx.serialization.Serializable

/**
 * Represents a daily habit to be tracked.
 *
 * @property id Unique identifier for the habit
 * @property name Name of the habit
 * @property description Detailed description of the habit
 * @property timesPerDay Number of times the habit should be performed per day
 */
@Serializable
data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val timesPerDay: Int
)
