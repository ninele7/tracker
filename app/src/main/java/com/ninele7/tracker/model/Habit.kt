package com.ninele7.tracker.model

import android.content.Context
import androidx.room.*
import com.ninele7.tracker.R
import java.io.Serializable
import java.util.UUID

interface HabitProperty : Serializable {
    val resource: Int
    fun getName(c: Context): String = c.getString(resource)
}

enum class HabitPriority(override val resource: Int) : HabitProperty {
    HIGH(R.string.high_priority_habit),
    MEDIUM(R.string.medium_priority_habit),
    LOW(R.string.low_priority_habit),
}

enum class HabitType(override val resource: Int, val emoji: String) : HabitProperty {
    GOOD(R.string.good_habit, "\uD83D\uDE42"),
    BAD(R.string.bad_habit, "\uD83D\uDE14");
}

class Converters {
    @TypeConverter
    fun fromHabitPriority(value: HabitPriority): Int = value.ordinal

    @TypeConverter
    fun toHabitPriority(value: Int): HabitPriority = HabitPriority.values()[value]

    @TypeConverter
    fun fromHabitType(value: HabitType): Int = value.ordinal

    @TypeConverter
    fun toHabitType(value: Int): HabitType = HabitType.values()[value]
}

@Entity
@TypeConverters(Converters::class)
data class Habit(
    @PrimaryKey
    var uid: UUID,
    var name: String,
    var description: String,
    var priority: HabitPriority,
    var type: HabitType,
    var period: Int,
    var timesPerPeriod: Int,
    var color: Int,
    val updated: Long,
) : Serializable {
    fun getTypeEmoji(): String = type.emoji
    fun getPriorityCardText(c: Context) =
        c.getString(R.string.priority_habit_view, priority.getName(c))

    fun getPeriodCardText(c: Context) =
        c.getString(R.string.period_habit_view, 0, timesPerPeriod, period)
}
