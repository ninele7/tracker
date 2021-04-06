package com.ninele7.tracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ninele7.tracker.databinding.EditHabitFragmentBinding

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

class EditHabitFragment : Fragment() {
    companion object {
        private const val INDEX_ARG = "INDEX_ARG"
        fun newInstance(): EditHabitFragment = EditHabitFragment()
        fun newInstance(index: Int): EditHabitFragment {
            val args = Bundle()
            args.putInt(INDEX_ARG, index)
            val fragment = EditHabitFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel by viewModels<EditHabitViewModel> {
        EditHabitViewModelFactory
    }
    private val mainViewModel by viewModels<HabitsViewModel> { HabitsViewModelFactory }
    private lateinit var prioritySpinner: AutoCompleteTextView
    private lateinit var typeRadio: RadioGroup
    private lateinit var binding: EditHabitFragmentBinding
    private lateinit var colorPicker: ColorPicker
    private val args: EditHabitFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_habit_fragment, container, false)
        binding.viewModel = viewModel
        val priorities = HabitPropertyArrayAdapter(
            inflater.context,
            R.layout.spinner_dropdown_item,
            HabitPriority.values()
        )
        prioritySpinner = binding.prioritySpinner
        prioritySpinner.setAdapter(priorities)
        typeRadio = binding.typeRadioGroup
        typeRadio.check(viewModel.typeId)
        if (viewModel.priorityId != -1)
            prioritySpinner.setText(HabitPriority.values()[viewModel.priorityId].resource)
        if (args.id != -1) {
            val habit = mainViewModel.habitList.find { it.id == args.id }
            if (habit != null) {
                viewModel.name = habit.name ?: ""
                viewModel.description = habit.description ?: ""
                viewModel.period = habit.period.toString()
                viewModel.timesPerPeriod = habit.timesPerPeriod.toString()
                binding.prioritySpinner.setText(habit.priority?.getName(inflater.context))
                viewModel.priorityId = habit.priority?.ordinal ?: 0
                typeRadio.check(if (habit.type == HabitType.GOOD) R.id.radioGood else R.id.radioBad)
                val newColor = habit.color
                if (newColor != null)
                    viewModel.observer.color = newColor
            }
        }
//        mainViewModel.title.value = R.string.edit_habit
        prioritySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> viewModel.priorityId = position }
        colorPicker = ColorPicker(binding.colorPicker, inflater.context)
        colorPicker.setOnSelectColor { color ->
            viewModel.observer.color = color
        }
        binding.button.setOnClickListener { onSaveButton(it) }
        return binding.root
    }

    override fun onDestroy() {
        viewModel.typeId = typeRadio.checkedRadioButtonId
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
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

    private fun populateHabit(habit: Habit) {
        habit.name = viewModel.name
        habit.description = viewModel.description
        habit.priority = HabitPriority.values()[viewModel.priorityId]
        habit.type =
            if (typeRadio.checkedRadioButtonId == R.id.radioGood) HabitType.GOOD else HabitType.BAD
        habit.period = viewModel.period.toInt()
        habit.timesPerPeriod = viewModel.timesPerPeriod.toInt()
        habit.color = viewModel.observer.color
    }

    private fun onSaveButton(view: View) {
        val validationResult = validateInput()
        if (validationResult != null) {
            Snackbar.make(view, validationResult, 2000).show()
            return
        }

        if (args.id != -1) {
            val habitToUpdate = mainViewModel.habitList.find { it.id == args.id }
            if (habitToUpdate != null) {
                populateHabit(habitToUpdate)
            }
        } else {
            val habit = Habit(mainViewModel.nextHabitId)
            populateHabit(habit)
            mainViewModel.habitList.add(habit)
        }
        mainViewModel.habitListLiveData.notifyObserver()
        findNavController().popBackStack()
    }
}
