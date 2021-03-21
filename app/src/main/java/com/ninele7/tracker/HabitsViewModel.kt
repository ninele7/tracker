package com.ninele7.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HabitsViewModel: ViewModel() {
    val habitList = mutableListOf<Habit>()
    var lastEditedHabit = -1
    val habitAdapter = HabitAdapter()
}

object HabitsViewModelFactory : ViewModelProvider.Factory {
    private val instance = HabitsViewModel()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return instance as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
