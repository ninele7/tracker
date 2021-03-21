package com.ninele7.tracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.google.android.material.snackbar.Snackbar
import com.ninele7.tracker.databinding.ActivityEditHabitBinding

class HabitPropertyArrayAdapter<T>(
    context: Context,
    private val layoutId: Int,
    private val items: Array<T>
) :
    ArrayAdapter<T>(context, layoutId, items) where T : HabitProperty {
    private val filter = HabitFilter(this)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItem = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)

        listItem.findViewById<TextView>(R.id.spinner_item_text).text =
            items[position].getName(context)
        return listItem
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private class HabitFilter<T>(private val adapter: HabitPropertyArrayAdapter<T>) :
        Filter() where T : HabitProperty {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = adapter.items
            results.count = adapter.count
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return when (resultValue) {
                is HabitProperty -> resultValue.getName(adapter.context)
                else -> ""
            }
        }
    }
}

const val HABIT_INTENT_VALUE = "habit_intent"

class EditHabitActivity : AppCompatActivity() {
    private val viewModel by viewModels<EditHabitViewModel> {
        EditHabitViewModelFactory
    }
    private lateinit var prioritySpinner: AutoCompleteTextView
    private lateinit var typeRadio: RadioGroup
    private lateinit var binding: ActivityEditHabitBinding
    private lateinit var colorPicker: ColorPicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_habit)
        binding.viewModel = viewModel
        val priorities = HabitPropertyArrayAdapter(
            this,
            R.layout.spinner_dropdown_item,
            HabitPriority.values()
        )
        prioritySpinner = binding.prioritySpinner
        prioritySpinner.setAdapter(priorities)
        typeRadio = binding.typeRadioGroup
        typeRadio.check(viewModel.typeId)
        if (viewModel.priorityId != -1)
            prioritySpinner.setText(HabitPriority.values()[viewModel.priorityId].resource)
        val habit = intent.getSerializableExtra(HABIT_INTENT_VALUE)
        if (habit is Habit) {
            viewModel.name = habit.name
            viewModel.description = habit.description
            viewModel.period = habit.period.toString()
            viewModel.timesPerPeriod = habit.timesPerPeriod.toString()
            binding.prioritySpinner.setText(habit.priority.getName(applicationContext))
            viewModel.priorityId = habit.priority.ordinal
            typeRadio.check(if (habit.type == HabitType.GOOD) R.id.radioGood else R.id.radioBad)
            viewModel.observer.color = habit.color
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.edit_habit)
        prioritySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> viewModel.priorityId = position }
        colorPicker = ColorPicker(binding.colorPicker, this)
        colorPicker.setOnSelectColor { color ->
            viewModel.observer.color = color
        }
    }

    override fun onDestroy() {
        viewModel.typeId = typeRadio.checkedRadioButtonId
        super.onDestroy()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EditHabitViewModelFactory.resetInstance()
    }

    private fun validateInput(): Int? {
        if (viewModel.name.isEmpty()) return R.string.no_name_for_habit
        if (viewModel.priorityId == -1) return R.string.no_priority_for_habit
        if (typeRadio.checkedRadioButtonId == -1) return R.string.no_type_for_habit
        if (viewModel.period.isEmpty()) return R.string.no_period_for_habit
        if (viewModel.timesPerPeriod.isEmpty()
        ) return R.string.no_times_per_period_for_habit
        return null
    }

    fun onSaveButton(view: View) {
        val resultIntent = Intent()
        val validationResult = validateInput()
        if (validationResult != null) {
            Snackbar.make(view, validationResult, 2000).show()
            return
        }
        val habit = Habit(
            // Validated in validateInput
            viewModel.name,
            viewModel.description,
            HabitPriority.values()[viewModel.priorityId],
            if (typeRadio.checkedRadioButtonId == R.id.radioGood) HabitType.GOOD else HabitType.BAD,
            viewModel.period.toInt(),
            viewModel.timesPerPeriod.toInt(),
            viewModel.observer.color
        )
        resultIntent.putExtra(HABIT_INTENT_VALUE, habit)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
