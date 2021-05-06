package com.ninele7.tracker.ui.edit.habit

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.ninele7.tracker.BR
import com.ninele7.tracker.R
import com.ninele7.tracker.model.DataSource
import com.ninele7.tracker.model.Habit
import com.ninele7.tracker.model.HabitPriority
import com.ninele7.tracker.model.HabitType
import kotlinx.coroutines.*

class EditHabitViewModel(private val dataSource: DataSource) : ViewModel() {
    var name = ""
    var description = ""
    var period = ""
    var timesPerPeriod = ""
    var openId = -1
    val priorityId = MutableLiveData(-1)
    val type = MutableLiveData<HabitType>(null)
    var navigator: EditHabitNavigator? = null

    val observer = Observer()

    fun loadHabitById(id: Int) {
        openId = id
        val habit = dataSource.habitsList.value?.firstOrNull { it.id == id }
        if (habit != null) {
            name = habit.name ?: ""
            description = habit.description ?: ""
            period = habit.period.toString()
            timesPerPeriod = habit.timesPerPeriod.toString()
            priorityId.value = habit.priority?.ordinal ?: -1
            type.value = habit.type
            val newColor = habit.color
            if (newColor != null)
                observer.color = newColor
        }
    }

    fun onViewCreated(nav: EditHabitNavigator) {
        navigator = nav
    }

    fun onViewDestroyed() {
        navigator = null
    }

    private fun validateInput(): Int? {
        if (name.isEmpty()) return R.string.no_name_for_habit
        if (priorityId.value == -1) return R.string.no_priority_for_habit
        if (type.value == null) return R.string.no_type_for_habit
        if (period.isEmpty()) return R.string.no_period_for_habit
        if (timesPerPeriod.isEmpty()
        ) return R.string.no_times_per_period_for_habit
        return null
    }

    fun onSaveButton(view: View) {
        val validationResult = validateInput()
        if (validationResult != null) {
            Snackbar.make(view, validationResult, 2000).show()
            return
        }
        viewModelScope.launch {
            if (openId != -1) {
                dataSource.updateHabit(createHabit(openId))
            } else {
                val habit = createHabit()
                dataSource.addHabit(habit)
            }
            navigator?.onSaved()
        }
    }

    private fun createHabit(id: Int = 0): Habit {
        val habit = Habit(id)
        habit.name = name
        habit.description = description
        val priorityIdValue = priorityId.value
        if (priorityIdValue != null)
            habit.priority = HabitPriority.values()[priorityIdValue]
        habit.type = type.value
        habit.period = period.toInt()
        habit.timesPerPeriod = timesPerPeriod.toInt()
        habit.color = observer.color
        return habit
    }

    class Observer : BaseObservable() {
        @get:Bindable
        var color: Int = Color.HSVToColor(floatArrayOf((1f / 32) * 360, 1f, 1f))
            set(value) {
                notifyPropertyChanged(BR.color)
                field = value
            }

        @Bindable("color")
        fun getHSVColor(): String {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            return "HSV(${hsv[0]}, ${hsv[1]}, ${hsv[2]})"
        }

        @Bindable("color")
        fun getRGBColor(): String {
            val r = color shr 16 and 0xff
            val g = color shr 8 and 0xff
            val b = color and 0xff
            return "RGB($r, $g, $b)"
        }
    }
}

object EditHabitViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditHabitViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return EditHabitViewModel(DataSource.getDataSource()) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
