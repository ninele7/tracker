package com.ninele7.tracker.ui.main

import androidx.lifecycle.*
import com.ninele7.tracker.R
import com.ninele7.tracker.model.DataSource
import com.ninele7.tracker.model.Habit
import com.ninele7.tracker.model.HabitType
import kotlinx.coroutines.launch

class HabitsViewModel(private val dataSource: DataSource) : ViewModel() {
    val filterString = MutableLiveData("")
    private val sortOrder = MutableLiveData<(Habit, Habit) -> Int>()

    private val habits: LiveData<List<Habit>> = dataSource.habitsList
        .product(filterString)
        .product(sortOrder)
        .map { (pair, order) ->
            if (pair != null) {
                val (list, filter) = pair
                val unsorted = list?.filter { it.name?.contains(filter ?: "") ?: true }
                (if (order == null) unsorted else unsorted?.sortedWith(order)) ?: listOf()
            } else listOf()
        }

    fun changeSortOrder(id: Int) {
        when (id) {
            R.id.radio_creation_time -> sortOrder.value = { h1, h2 ->
                h1.id - h2.id
            }
            R.id.radio_name -> sortOrder.value = { h1, h2 ->
                h2.name?.let { h1.name?.compareTo(it) } ?: 1
            }
            else -> {
            }
        }
    }

    fun getFilteredHabits(type: HabitType?): LiveData<List<Habit>> {
        if (type == null) return habits
        return habits.map { it.filter { h -> h.type == type } }
    }

    fun removeHabit(id: Int) = viewModelScope.launch { dataSource.removeHabit(id) }
}

object HabitsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return HabitsViewModel(DataSource.getDataSource()) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun <T1, T2> LiveData<T1>.map(f: (T1) -> T2) = Transformations.map(this, f)

fun <T1, T2> LiveData<T1>.product(other: LiveData<T2>): LiveData<Pair<T1?, T2?>> {
    val mediator = MediatorLiveData<Pair<T1?, T2?>>()
    mediator.addSource(this) { mediator.value = it to other.value }
    mediator.addSource(other) { mediator.value = this.value to it }
    return mediator
}