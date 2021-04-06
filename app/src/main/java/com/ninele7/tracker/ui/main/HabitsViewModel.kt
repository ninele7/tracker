package com.ninele7.tracker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HabitsViewModel: ViewModel() {
    val habitList = mutableListOf<Habit>()
    val habitListLiveData = MutableLiveData<List<Habit>>(habitList)
    var nextHabitId: Int = 0
        get() {
            field++
            return field
        }
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

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}
