package com.ninele7.tracker.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.ninele7.tracker.R
import com.ninele7.tracker.model.DataSource
import com.ninele7.tracker.model.Habit
import com.ninele7.tracker.model.HabitType
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.sign

class HabitsViewModel(private val dataSource: DataSource) : ViewModel() {
    val filterString = MutableLiveData("")
    private val sortOrder = MutableLiveData<(Habit, Habit) -> Int>()
    private var callback: HabitsCallback? = null

    private val habits: LiveData<List<Habit>> =
        dataSource.habitsList.asLiveData().product(filterString).product(sortOrder)
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
        if (type == null) return habits
        return habits.map { it.filter { h -> h.type == type } }
    }

    fun removeHabit(id: UUID, failureCallback: () -> Unit): Job =
        viewModelScope.async {
            try {
                dataSource.removeHabit(id)
            } catch (e: Exception) {
                Log.e("HabitsViewModel", "removeHabit", e)
                callback?.onError()
                failureCallback()
            }
        }

    fun forceSync(): Job =
        viewModelScope.launch {
            try {
                dataSource.forceSync()
            } catch (e: Exception) {
                Log.e("HabitsViewModel", "forceSync", e)
                callback?.onError()
            }
        }
}

object HabitsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java)) @Suppress("UNCHECKED_CAST") return HabitsViewModel(
            DataSource.getDataSource()
        ) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun <T1, T2> LiveData<T1>.product(other: LiveData<T2>): LiveData<Pair<T1?, T2?>> {
    val mediator = MediatorLiveData<Pair<T1?, T2?>>()
    mediator.addSource(this) { mediator.value = it to other.value }
    mediator.addSource(other) { mediator.value = this.value to it }
    return mediator
}
