package pt.isel.pdm.chatr.domain

import kotlinx.serialization.Serializable

/**
 * Representa a completion de um hábito num dia específico
 *
 * @property habitId Identificador do hábito ao qual esta completion se refere
 * @property date Data da completion no formato "YYYY-MM-DD"
 * @property completedTimes Número de vezes que o hábito foi completado nesse dia
 */
@Serializable
data class HabitCompletion(
    val habitId: String,
    val date: String,
    val completedTimes: Int
)
