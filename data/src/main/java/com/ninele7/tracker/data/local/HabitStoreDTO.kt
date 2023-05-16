package com.ninele7.tracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitPriority
import com.ninele7.tracker.domain.habit.HabitType
import java.util.*

@Entity(tableName = "habit")
data class HabitStoreDTO(
    @PrimaryKey
    var uid: UUID,
    var name: String,
    var description: String,
    var priority: Int,
    var type: Int,
    var period: Int,
    var timesPerPeriod: Int,
    var color: Int,
    val updated: Long,
) {
    fun toModel() =
        Habit(
            uid,
            name,
            description,
            HabitPriority.fromInt(priority),
            HabitType.fromInt(type),
            period,
            timesPerPeriod,
            color,
            updated
        )

    companion object {
        fun fromModel(habit: Habit) =
            HabitStoreDTO(
                habit.uid,
                habit.name,
                habit.description,
                habit.priority.ordinal,
                habit.type.ordinal,
                habit.period,
                habit.timesPerPeriod,
                habit.color,
                habit.updated
            )
    }
}