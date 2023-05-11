package com.ninele7.tracker.presentation.viewmodel.main

import android.util.Log
import androidx.lifecycle.*
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitInteractor
import com.ninele7.tracker.domain.habit.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class HabitsViewModel @Inject constructor(private val habitInteractor: HabitInteractor) :
    ViewModel() {
    val filterString = MutableLiveData("")
    private val sortOrder = MutableLiveData<(Habit, Habit) -> Int>()
    private var callback: HabitsCallback? = null

    private fun habits(): LiveData<List<Habit>> =
        habitInteractor.allLocalHabits().asLiveData().product(filterString).product(sortOrder)
            .map { (pair, order) ->
                if (pair != null) {
                    val (list, filter) = pair
                    val unsorted = list?.filter { it.name.contains(filter ?: "") }
                    (if (order == null) unsorted else unsorted?.sortedWith(order)) ?: listOf()
                } else listOf()
            }

    fun onViewCreated(callback: HabitsCallback) {
        this.callback = callback
    }

    fun changeSortOrder(id: Int) {
        when (id) {
            R.id.radio_creation_time -> sortOrder.value = { h1, h2 ->
                (h1.updated - h2.updated).sign
            }

            R.id.radio_name -> sortOrder.value = { h1, h2 ->
                h2.name.let { h1.name.compareTo(it) }
            }

            else -> {
            }
        }
    }

    fun getFilteredHabits(type: HabitType?): LiveData<List<Habit>> {
        if (type == null) return habits()
        return habits().map { it.filter { h -> h.type == type } }
    }

    fun removeHabit(id: UUID, failureCallback: () -> Unit): Job =
        viewModelScope.async {
            try {
                habitInteractor.removeHabit(id)
            } catch (e: Exception) {
                Log.e("HabitsViewModel", "removeHabit", e)
                callback?.onError()
                failureCallback()
            }
        }

    fun forceSync(): Job =
        viewModelScope.launch {
            try {
                habitInteractor.forceSync()
            } catch (e: Exception) {
                Log.e("HabitsViewModel", "forceSync", e)
                callback?.onError()
            }
        }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                habitInteractor.completeHabit(habit.uid)
                callback?.onHabitCompleted(
                    habit.timesPerPeriod - habit.completions.size - 1,
                    habit.type
                )
            } catch (e: Exception) {
                Log.e("HabitsViewModel", "completeHabit", e)
                callback?.onError()
            }
        }
    }
}

fun <T1, T2> LiveData<T1>.product(other: LiveData<T2>): LiveData<Pair<T1?, T2?>> {
    val mediator = MediatorLiveData<Pair<T1?, T2?>>()
    mediator.addSource(this) { mediator.value = it to other.value }
    mediator.addSource(other) { mediator.value = this.value to it }
    return mediator
}
