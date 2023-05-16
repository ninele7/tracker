package com.ninele7.tracker.presentation.viewmodel.main

import com.ninele7.tracker.domain.habit.HabitType

interface HabitsCallback {
    fun onError()
    fun onHabitCompleted(i: Int, type: HabitType)
}