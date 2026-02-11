package pt.isel.pdm.chatr.domain

import kotlinx.serialization.Serializable

/**
 * Representa um hábito
 *
 * @property id Identificador do hábito (único)
 * @property name Nome do hábito
 * @property description Descrição do hábito
 * @property timesPerDay Número de vezes que o hábito deve ser realizado por dia
 */
@Serializable
data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val timesPerDay: Int
)
