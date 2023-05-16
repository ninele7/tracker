package com.ninele7.tracker.data.remote

import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitPriority
import com.ninele7.tracker.domain.habit.HabitType
import java.util.*

data class HabitRemoteDTO(
    val color: Int,
    val count: Int,
    val date: Long,
    val description: String,
    val doneDates: List<Long>,
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val uid: UUID?
) {
    companion object {
        fun fromModel(habit: Habit): HabitRemoteDTO {
            return HabitRemoteDTO(
                habit.color,
                habit.timesPerPeriod,
                habit.updated,
                habit.description,
                habit.completions,
                habit.period,
                habit.priority.ordinal,
                habit.name,
                habit.type.ordinal,
                habit.uid
            )
        }
    }

    fun toModel(): Habit {
        return Habit(
            updated = date,
            uid = uid ?: UUID.randomUUID(),
            name = title,
            description = description,
            priority = HabitPriority.values()[priority],
            type = HabitType.values()[type],
            period = count,
            timesPerPeriod = frequency,
            color = color,
            completions = doneDates
        )
    }
}