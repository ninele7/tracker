package com.ninele7.tracker.presentation.viewmodel.edit

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.ninele7.tracker.BR
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitInteractor
import com.ninele7.tracker.domain.habit.HabitPriority
import com.ninele7.tracker.domain.habit.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(private val habitInteractor: HabitInteractor) :
    ViewModel() {
    private var openId: UUID? = null
    private var callback: EditHabitCallback? = null
    val priorityId = MutableLiveData(-1)
    val type = MutableLiveData<HabitType>(null)

    val observer = Observer()

    fun loadHabitById(id: UUID) {
        openId = id
        viewModelScope.launch {
            val habit = habitInteractor.getHabit(id)
            Log.i("EditHabitViewModel", "loadHabitById: $habit")
            if (habit != null) {
                observer.name = habit.name
                observer.description = habit.description
                observer.period = habit.period.toString()
                observer.timesPerPeriod = habit.timesPerPeriod.toString()
                priorityId.value = habit.priority.ordinal
                type.value = habit.type
                observer.color = habit.color
                observer.notifyChange()
            }
        }
    }

    fun onViewCreated(callback: EditHabitCallback) {
        this.callback = callback
    }

    fun onViewDestroyed() {
        callback = null
    }

    private fun validateInput(): Int? {
        if (observer.name.isEmpty()) return R.string.no_name_for_habit
        if (priorityId.value == -1) return R.string.no_priority_for_habit
        if (type.value == null) return R.string.no_type_for_habit
        if (observer.period.isEmpty()) return R.string.no_period_for_habit
        if (observer.timesPerPeriod.isEmpty()
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
            try {
                if (openId != null) {
                    habitInteractor.updateHabit(createHabit())
                } else {
                    val habit = createHabit()
                    habitInteractor.addHabit(habit)
                }
                callback?.onSaved()
            } catch (e: Exception) {
                callback?.onError()
            }
        }
    }

    private fun createHabit(): Habit {
        val newHabitName = observer.name
        val newHabitDescription = observer.description
        val priorityIdValue = priorityId.value
        val newHabitPriority = if (priorityIdValue != -1 && priorityIdValue != null)
            HabitPriority.values()[priorityIdValue]
        else HabitPriority.MEDIUM
        val newHabitType = type.value ?: HabitType.GOOD
        val newHabitPeriod = observer.period.toInt()
        val newHabitTimesPerPeriod = observer.timesPerPeriod.toInt()
        val newHabitColor = observer.color
        return Habit(
            updated = System.currentTimeMillis(),
            uid = openId ?: UUID.randomUUID(),
            name = newHabitName,
            description = newHabitDescription,
            priority = newHabitPriority,
            type = newHabitType,
            period = newHabitPeriod,
            timesPerPeriod = newHabitTimesPerPeriod,
            color = newHabitColor
        )
    }

    class Observer : BaseObservable() {
        @Bindable
        var name = ""

        @Bindable
        var description = ""

        @Bindable
        var period = ""

        @Bindable
        var timesPerPeriod = ""

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
