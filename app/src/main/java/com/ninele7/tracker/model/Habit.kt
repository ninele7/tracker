package com.ninele7.tracker.model

import android.content.Context
import com.ninele7.tracker.R
import java.io.Serializable

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

data class Habit(
    val id: Int,
    var name: String? = null,
    var description: String? = null,
    var priority: HabitPriority? = null,
    var type: HabitType? = null,
    var period: Int? = null,
    var timesPerPeriod: Int? = null,
    var color: Int? = null
) : Serializable {
    fun getPriorityText(c: Context): String? = priority?.getName(c)
    fun getTypeText(c: Context): String? = type?.getName(c)
    fun getTypeEmoji(): String? = type?.emoji
    fun getPriorityCardText(c: Context) =
        c.getString(R.string.priority_habit_view, priority?.getName(c))

    fun getPeriodCardText(c: Context) =
        c.getString(R.string.period_habit_view, 0, timesPerPeriod, period)
}
