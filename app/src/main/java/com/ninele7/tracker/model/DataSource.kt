package com.ninele7.tracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataSource {
    private val habitsLiveData = MutableLiveData(listOf<Habit>())
    var nextHabitId: Int = 0
        get() {
            field++
            return field
        }

    fun getHabitsList(): LiveData<List<Habit>> = habitsLiveData

    fun addHabit(habit: Habit) {
        habitsLiveData.value = habitsLiveData.value?.plus(habit)
    }

    fun removeHabit(id: Int) {
        val mutable = habitsLiveData.value?.toMutableList() ?: return
        mutable.removeIf { it.id == id }
        habitsLiveData.value = mutable
    }

    fun updateHabit(habit: Habit) {
        val mutable = habitsLiveData.value?.toMutableList() ?: return
        val index = mutable.indexOfFirst { it.id == habit.id }
        if (index == -1) throw NoSuchElementException("Habit wasn't in the list")
        mutable[index] = habit
        habitsLiveData.value = mutable
    }

    fun getHabit(id: Int): Habit? = habitsLiveData.value?.firstOrNull { it.id == id }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}