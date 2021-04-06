package com.ninele7.tracker

import android.graphics.Color
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditHabitViewModel : ViewModel() {
    var name = ""
    var description = ""
    var period = ""
    var timesPerPeriod = ""
    var priorityId = -1
    var typeId = -1

    val observer = Observer()

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
    private var instance = EditHabitViewModel()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditHabitViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return instance as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    fun resetInstance() {
        instance = EditHabitViewModel()
    }
}
