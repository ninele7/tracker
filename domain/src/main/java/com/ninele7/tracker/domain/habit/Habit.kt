package com.ninele7.tracker.domain.habit

import java.util.UUID

sealed interface HabitProperty

enum class HabitPriority : HabitProperty {
    HIGH, MEDIUM, LOW;

    companion object {
        fun fromInt(index: Int) = values()[index]
    }
}

enum class HabitType : HabitProperty {
    GOOD, BAD;

    companion object {
        fun fromInt(index: Int) = values()[index]
    }
}

data class Habit(
    val uid: UUID,
    val name: String,
    val description: String,
    val priority: HabitPriority,
    val type: HabitType,
    val period: Int,
    val timesPerPeriod: Int,
    val color: Int,
    val updated: Long,
    val completions: List<Long> = emptyList()
)
