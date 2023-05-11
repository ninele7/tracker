package com.ninele7.tracker.presentation

import android.content.Context
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitPriority
import com.ninele7.tracker.domain.habit.HabitProperty
import com.ninele7.tracker.domain.habit.HabitType

object Mappings {
    fun Habit.getPriorityCardText(c: Context) =
        c.getString(R.string.priority_habit_view, priority.getName(c))

    fun Habit.getPeriodCardText(c: Context) =
        c.getString(R.string.period_habit_view, completions.size, timesPerPeriod, period)

    private fun HabitPriority.getResource() = when (this) {
        HabitPriority.HIGH -> R.string.high_priority_habit
        HabitPriority.MEDIUM -> R.string.medium_priority_habit
        HabitPriority.LOW -> R.string.low_priority_habit
    }

    fun HabitPriority.getName(c: Context) = c.getString(getResource())

    private fun HabitType.getResource() = when (this) {
        HabitType.GOOD -> R.string.good_habit
        HabitType.BAD -> R.string.bad_habit
    }

    fun HabitType.getEmoji() = when (this) {
        HabitType.GOOD -> "\uD83D\uDE42"
        HabitType.BAD -> "\uD83D\uDE14"
    }

    fun HabitProperty.getName(c: Context) = when (this) {
        is HabitPriority -> getName(c)
        is HabitType -> c.getString(getResource())
    }
}