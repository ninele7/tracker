package com.ninele7.tracker.ui.main

import androidx.lifecycle.*
import com.ninele7.tracker.R
import com.ninele7.tracker.model.DataSource
import com.ninele7.tracker.model.Habit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitsViewModel(private val dataSource: DataSource) : ViewModel() {
    val filterString = MutableLiveData("")
    val sortOrder = MutableLiveData<(Habit, Habit) -> Int>()
    val habits =
        Transformations.map(TripleTrigger(dataSource.habitsList, filterString, sortOrder)) {
            val unsorted =
                it.first?.filter { habit -> habit.name?.contains(it.second ?: "") ?: true }
            val sortOrder = it.third
            if (sortOrder == null) unsorted
            else unsorted?.sortedWith(sortOrder)
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

    fun removeHabit(id: Int) = GlobalScope.launch { dataSource.removeHabit(id) }
}

object HabitsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return HabitsViewModel(DataSource.getDataSource()) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TripleTrigger<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>) :
    MediatorLiveData<Triple<A?, B?, C?>>() {
    init {
        addSource(a) { value = Triple(it, b.value, c.value) }
        addSource(b) { value = Triple(a.value, it, c.value) }
        addSource(c) { value = Triple(a.value, b.value, it) }
    }
}
